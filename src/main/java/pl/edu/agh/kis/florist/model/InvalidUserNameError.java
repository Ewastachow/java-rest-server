package pl.edu.agh.kis.florist.model;

import java.util.Map;

/**
 * Created by yevvye on 26.01.17.
 */
public class InvalidUserNameError {
    public InvalidUserNameError(Map<String, String> params) {
        setParams(params);
        init();
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
        init();
    }

    private void init() {
        this.message = String.format("Invalid user name: %s",params);
    }

    private String message;
    private Map<String, String> params;
}
