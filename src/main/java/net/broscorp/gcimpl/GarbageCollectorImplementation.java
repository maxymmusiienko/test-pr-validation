package net.broscorp.gcimpl;

import java.util.*;

public class GarbageCollectorImplementation implements GarbageCollector {
    @Override
    public List<ApplicationBean> collect(HeapInfo heap, StackInfo stack) {
        Collection<ApplicationBean> beans = heap.getBeans().values();
        Deque<StackInfo.Frame> frames = stack.getStack();
        Set<ApplicationBean> reachableObjects = new HashSet<>();
        Set<ApplicationBean> visited = new HashSet<>();

        // Traverse the stack frames
        for (StackInfo.Frame frame : frames) {
            reachableObjects.addAll(frame.getParameters());
            for (ApplicationBean param : frame.getParameters()) {
                // collect child objects
                collectChildObjects(param, reachableObjects,visited);
            }
        }
        // Identify unreachable objects
        List<ApplicationBean> garbageObjects = new ArrayList<>(beans);
        garbageObjects.removeAll(reachableObjects);
        return garbageObjects;

    }
    private void collectChildObjects(ApplicationBean root, Set<ApplicationBean> reachableObjects, Set<ApplicationBean> visited) {
        Deque<ApplicationBean> stack = new LinkedList<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            ApplicationBean current = stack.pop();

            if (!visited.contains(current)) {
                visited.add(current);
                for (ApplicationBean child : current.getFieldValues().values()) {
                    if (!reachableObjects.contains(child)) {
                        reachableObjects.add(child);
                        stack.push(child);
                    }
                }
            }
        }
    }
}