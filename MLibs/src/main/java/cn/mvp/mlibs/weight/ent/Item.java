package cn.mvp.mlibs.weight.ent;

public class Item<T> {
    private String val;
    private T data;

    public Item(String val, T data) {
        this.val = val;
        this.data = data;
    }


    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}