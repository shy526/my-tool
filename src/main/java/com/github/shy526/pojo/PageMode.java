package com.github.shy526.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * 分页模型
 *
 * @author shy526
 */
public class PageMode<T> implements Serializable {
    /**
     * 总数
     */
    private Integer total;
    /**
     * 页码
     */
    private Integer pageNumber = 1;
    /**
     * 每页记录数
     */
    private Integer pageSize;
    /**
     * 当前索引
     */
    private Integer nowIndex = 0;
    /**
     * 数据集合
     */
    private List<T> dataList;

    /**
     * 总页数
     */
    private Integer totalPageNumber;

    /**
     * 下一页
     */
    public void next() {
        this.pageNumber++;
        this.countNowIndex();
    }

    /**
     * 上一页
     */
    public void prev() {
        if (this.pageNumber > 1) {
            this.pageNumber--;
            this.countNowIndex();
        }
    }

    /**
     * 计算当前索引位置
     */
    private void countNowIndex() {
        this.nowIndex = (pageNumber - 1) * pageSize;
    }

    /**
     * 放入当前页的数据 并翻页
     *
     * @param dataList 数据集合
     */
    public void setDatAndNext(List<T> dataList) {
        this.dataList = dataList;
        this.next();
    }


    public void setTotal(Integer total) {
        this.total = total;
        this.countTotalPageNumber();

    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
        this.countNowIndex();
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        this.countTotalPageNumber();
    }

    /**
     * 计算总页数
     */
    private void countTotalPageNumber() {
        this.totalPageNumber = ((int) Math.ceil(((double) this.total) / this.pageSize));
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }


    public Integer getTotal() {
        return total;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public Integer getNowIndex() {
        return nowIndex;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public Integer getTotalPageNumber() {
        return totalPageNumber;
    }
}

