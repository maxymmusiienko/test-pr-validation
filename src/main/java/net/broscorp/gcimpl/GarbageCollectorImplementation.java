package net.broscorp.gcimpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GarbageCollectorImplementation implements GarbageCollector {

    // list for collecting garbage
    private List<ApplicationBean> garbage;

    // list for tracking processed beans to avoid circular ref
    private Set<ApplicationBean> processed;

    @Override
    public List<ApplicationBean> collect(HeapInfo heap, StackInfo stack) {
        // making a garbage list without repeating beans
        garbage = new ArrayList<>();
        heap.getBeans()
                .values()
                .forEach(
                        bean -> {
                            if (!garbage.contains(bean)) {
                                garbage.add(bean);
                            }
                        });

        // here we remove all called beans and their children from the garbageList
        processed = new HashSet<>();
        while (!stack.getStack().isEmpty()) {
            StackInfo.Frame currFrame = stack.pop();
            for (ApplicationBean param : currFrame.getParameters()) {
                removeFromGarbage(param);
            }
        }

        // clearing the used list to free the memory
        processed = null;

        // returning found garbage beans
        return garbage;
    }

    private void removeFromGarbage(ApplicationBean bean) {
        // removing bean from garbage and adding it to the processed list
        garbage.remove(bean);
        processed.add(bean);

        // executing the same thing for the bean children
        bean.getFieldValues()
                .values()
                .forEach(
                        child -> {
                            if (!processed.contains(child)) { // if the child was not processed already
                                removeFromGarbage(child);
                            }
                        });
    }

    private void method(String t) {
        System.out.println(t + 5);
    }
}