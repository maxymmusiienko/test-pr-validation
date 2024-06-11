package net.broscorp.gcimpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GarbageCollectorImplementation implements GarbageCollector {

    private Set<ApplicationBean> markedBeans;

    {
        markedBeans = new HashSet<>();
    }

    @Override
    public List<ApplicationBean> collect(HeapInfo heap, StackInfo stack) {
        markedBeans.clear();
        System.out.println("cleared");
        stack.getStack().forEach(frame -> frame.getParameters().forEach(this::mark));

        List<ApplicationBean> garbage = new ArrayList<>();
        heap.getBeans().values().forEach(bean -> {
            if (!markedBeans.contains(bean)) {
                garbage.add(bean);
            }
        });

        return garbage;
    }

    private void mark(ApplicationBean bean) {
        if (bean == null || markedBeans.contains(bean)) {
            return;
        }

        markedBeans.add(bean);
        List<ApplicationBean> beansToMark = new ArrayList<>();
        beansToMark.add(bean);
        while (!beansToMark.isEmpty()) {
            ApplicationBean beanToMark = beansToMark.remove(0);
            beanToMark.getFieldValues().values().forEach(value -> {
                if (value instanceof ApplicationBean) {
                    ApplicationBean beanValue = (ApplicationBean) value;
                    if (!markedBeans.contains(beanValue)) {
                        markedBeans.add(beanValue);
                        beansToMark.add(beanValue);
                    }
                }
            });
        }
    }

    private int g() {
        return 5;
    }
}
