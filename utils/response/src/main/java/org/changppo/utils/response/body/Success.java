package org.changppo.utils.response.body;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Success<T> implements Result{
    private T data;
}
