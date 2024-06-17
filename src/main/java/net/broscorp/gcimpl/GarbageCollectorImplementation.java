package net.broscorp.gcimpl;

import java.util.ArrayList;
import java.util.Collection;
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

        registerApplicationBeans(stackDeque, stack);
        findReachableObjects(stackDeque, reachableObjects);
        filterUnreachableObjects(unreachableObjects, heap.getBeans().values(), reachableObjects);

        return unreachableObjects;
    }

    private void registerApplicationBeans(Deque<ApplicationBean> stackDeque,
                                          StackInfo stack) {
        for (StackInfo.Frame frame : stack.getStack()) {
            stackDeque.addAll(frame.getParameters());
        }
    }

    private void findReachableObjects(Deque<ApplicationBean> stackDeque,
                                      Set<ApplicationBean> reachableObjects) {
        while (!stackDeque.isEmpty()) {
            ApplicationBean currentBean = stackDeque.pop();
            if (currentBean != null && reachableObjects.add(currentBean)) {
                stackDeque.addAll(currentBean.getFieldValues().values());
            }
        }
    }

    private void filterUnreachableObjects(List<ApplicationBean> unreachableObjects,
                                          Collection<ApplicationBean> beans,
                                          Set<ApplicationBean> reachableObjects) {
        unreachableObjects.addAll(beans);
        unreachableObjects.removeAll(reachableObjects);
    }
}