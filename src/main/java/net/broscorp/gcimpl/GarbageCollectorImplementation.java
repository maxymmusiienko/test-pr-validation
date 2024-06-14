package net.broscorp.gcimpl;

import java.util.*;

public class GarbageCollectorImplementation implements GarbageCollector {

    @Override
    public List<ApplicationBean> collect(HeapInfo heap, StackInfo stack) {
        Collection<ApplicationBean> beans = heap.getBeans().values();
        Deque<StackInfo.Frame> frames = stack.getStack();

        Set<ApplicationBean> reachableBeans = new HashSet<>();
        Set<ApplicationBean> visitedBeans = new HashSet<>();

        // Gather reachable beans from stack frames
        for (StackInfo.Frame frame : frames) {
            if (!frame.getParameters().isEmpty()) {
                ApplicationBean child = frame.getParameters().get(0);
                Queue<ApplicationBean> queue = new LinkedList<>();
                queue.offer(child);

                while (!queue.isEmpty()) {
                    ApplicationBean current = queue.poll();

                    if (visitedBeans.contains(current))
                        continue;

                    reachableBeans.add(current);
                    visitedBeans.add(current);

                    for (ApplicationBean bean : current.getFieldValues().values())
                        if (!visitedBeans.contains(bean))
                            queue.offer(bean);
                }
            }
        }

        // Identify garbage in the heap
        List<ApplicationBean> garbage = new ArrayList<>();
        for (ApplicationBean bean : beans) {
            if (!reachableBeans.contains(bean)) {
                garbage.add(bean);
            }
        }

        return garbage;
    }
}