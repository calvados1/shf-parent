package com.atguigu.en;

public enum DictCode {
    ROOT("ROOT"), HOUSETYPE("houseType"), FLOOR("floor"), BUILDSTRUCTURE("buildStructure"),
    DECORATION("decoration"), DIRECTION("direction"), HOUSEUSE("houseUse"),
    PROVINCE("province"), BEIJING("beijing");
    public String code;

    DictCode(String code) {
        this.code = code;
    }

}