package net.broscorp.gcimpl;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GarbageCollectorImplementation implements GarbageCollector {

    @Override
    public List<ApplicationBean> collect(HeapInfo heap, StackInfo stack) {
        List<ApplicationBean> reachableObjects = new ArrayList<>();
        Set<ApplicationBean> visited = new HashSet<>();

        for (StackInfo.Frame frame : stack.getStack()) {
            for (ApplicationBean param : frame.getParameters()) {
                markReachable(param, reachableObjects, heap.getBeans(), visited);
            }
        }

        List<ApplicationBean> garbage = new ArrayList<>(heap.getBeans().values());
        garbage.removeAll(reachableObjects);
        return garbage;
    }

    private void markReachable(ApplicationBean obj, List<ApplicationBean> reachableObjects,
                               Map<String, ApplicationBean> heap, Set<ApplicationBean> visited) {
        Deque<ApplicationBean> stack = new LinkedList<>();
        stack.push(obj);

        while (!stack.isEmpty()) {
            ApplicationBean current = stack.pop();
            if (visited.contains(current)) {
                continue;
            }
            visited.add(current);
            reachableObjects.add(current);

            for (ApplicationBean child : current.getFieldValues().values()) {
                if (heap.containsValue(child)) {
                    stack.push(child);
                }
            }
        }
    }
}
