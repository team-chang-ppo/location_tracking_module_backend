package org.changppo.account.batch.config.reader;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.adapter.AbstractMethodInvokingDelegator;
import org.springframework.batch.item.adapter.DynamicMethodInvocationException;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.util.MethodInvoker;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class RepositoryZeroPagingItemReader<T> extends RepositoryItemReader<T> {
    private PagingAndSortingRepository<?, ?> repository;
    private List<?> arguments;
    private String methodName;
    private int pageSize;
    private Sort sort;

    public RepositoryZeroPagingItemReader(PagingAndSortingRepository<?, ?> repository, String methodName, List<?> arguments, int pageSize, Map<String, Sort.Direction> sort) {
        this.sort = convertToSort(sort);
        this.pageSize = pageSize;
        this.methodName = methodName;
        this.arguments = arguments;
        this.repository = repository;
        super.setRepository(repository);
        super.setMethodName(methodName);
        super.setArguments(arguments);
        super.setPageSize(pageSize);
        super.setSort(sort);
    }
    private Sort convertToSort(Map<String, Sort.Direction> sorts) {
        List<Sort.Order> sortValues = new ArrayList();
        Iterator var3 = sorts.entrySet().iterator();

        while(var3.hasNext()) {
            Map.Entry<String, Sort.Direction> curSort = (Map.Entry)var3.next();
            sortValues.add(new Sort.Order((Sort.Direction)curSort.getValue(), (String)curSort.getKey()));
        }

        return Sort.by(sortValues);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected List<T> doPageRead() throws Exception {
        Pageable pageRequest = PageRequest.of(0, this.pageSize, this.sort);
        MethodInvoker invoker = createMethodInvoker(this.repository, this.methodName);
        List<Object> parameters = new ArrayList();
        if (this.arguments != null && this.arguments.size() > 0) {
            parameters.addAll(this.arguments);
        }

        parameters.add(pageRequest);
        invoker.setArguments(parameters.toArray());
        Slice<T> curPage = (Slice)this.doInvoke(invoker);
        return curPage.getContent();
    }

    private Object doInvoke(MethodInvoker invoker) throws Exception {
        try {
            invoker.prepare();
        } catch (NoSuchMethodException | ClassNotFoundException var3) {
            ReflectiveOperationException e = var3;
            throw new DynamicMethodInvocationException(e);
        }

        try {
            return invoker.invoke();
        } catch (InvocationTargetException var4) {
            InvocationTargetException e = var4;
            if (e.getCause() instanceof Exception) {
                throw (Exception)e.getCause();
            } else {
                throw new AbstractMethodInvokingDelegator.InvocationTargetThrowableWrapper(e.getCause());
            }
        } catch (IllegalAccessException var5) {
            IllegalAccessException e = var5;
            throw new DynamicMethodInvocationException(e);
        }
    }

    private MethodInvoker createMethodInvoker(Object targetObject, String targetMethod) {
        MethodInvoker invoker = new MethodInvoker();
        invoker.setTargetObject(targetObject);
        invoker.setTargetMethod(targetMethod);
        return invoker;
    }
}
