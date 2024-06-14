package net.broscorp.gcimpl;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class GarbageCollectorImplementation implements GarbageCollector {

    @Override
    public List<ApplicationBean> collect(HeapInfo heap, StackInfo stack) {
        List<ApplicationBean> unreachableObjects = new ArrayList<>();
        Set<ApplicationBean> reachableObjects = new HashSet<>();
        Deque<ApplicationBean> stackDeque = new LinkedList<>();

        for (StackInfo.Frame frame : stack.getStack()) {
            for (ApplicationBean parameter : frame.getParameters()) {
                stackDeque.push(parameter);
                while (!stackDeque.isEmpty()) {
                    ApplicationBean currentBean = stackDeque.pop();
                    if (currentBean != null && !reachableObjects.contains(currentBean)) {
                        reachableObjects.add(currentBean);
                        stackDeque.addAll(currentBean.getFieldValues().values());
                    }
                }
            }
        }

        for (ApplicationBean bean : heap.getBeans().values()) {
            if (!reachableObjects.contains(bean)) {
                unreachableObjects.add(bean);
            }
        }

        return unreachableObjects;
    }
}