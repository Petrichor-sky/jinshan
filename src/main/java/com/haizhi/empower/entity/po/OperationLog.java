package com.haizhi.empower.entity.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author by fuhanchao
 * @date 2022/12/9.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "operation_log")
public class OperationLog implements Serializable {

    public static final long serialVersion= 1L;
    @Id
    private String id;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "level")
    private Integer level;

    @Column(name = "operation_unit")
    private String operationUnit;

    @Column(name = "method")
    private String method;

    @Column(name = "args")
    private String args;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "describes")
    private String describes;

    @Column(name = "operation_type")
    private String operationType;

    @Column(name = "run_time")
    private String runTime;

    @Column(name = "return_value")
    private String returnValue;
}
