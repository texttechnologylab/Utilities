package org.texttechnologylab.utilities.process.model.success;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by abrami on 16.08.16.
 */
public class Success<T> {

    @ApiModelProperty(example = "true", required = true)
    private boolean success;

    private T result;

    public boolean getSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setResult(T pResult){
        this.result = pResult;
    }
    public T getResult(){ return this.result; }

}
