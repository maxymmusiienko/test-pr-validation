package net.broscorp.gcimpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GarbageCollectorImplementation implements GarbageCollector {

    @Override
    public List<ApplicationBean> collect(HeapInfo heap, StackInfo stack) {
        if (heap == null || stack == null) {
            throw new IllegalArgumentException("Heap and Stack cannot be null");
        }
        List<ApplicationBean> unreachableObjects = new ArrayList<>();
        Set<ApplicationBean> reachableObjects = new HashSet<>();
        Set<ApplicationBean> stackDeque = new HashSet<>();

        populateStackDeque(stackDeque, stack);
        findReachableObjects(stackDeque, reachableObjects);
        filterUnreachableObjects(unreachableObjects, heap.getBeans().values(), reachableObjects);

        return unreachableObjects;
    }

    private void populateStackDeque(Set<ApplicationBean> stackDeque,
                                          StackInfo stack) {
        stack.getStack().stream()
                .flatMap(frame -> frame.getParameters().stream())
                .forEach(stackDeque::add);
    }

    private void findReachableObjects(Set<ApplicationBean> stackDeque,
                                      Set<ApplicationBean> reachableObjects) {
        while (!stackDeque.isEmpty()) {
            ApplicationBean currentBean = stackDeque.iterator().next();
            stackDeque.remove(currentBean);
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