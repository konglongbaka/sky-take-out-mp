package com.sky.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName log_table
 */
@TableName(value ="log_table")
@Data
public class LogTable implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 
     */
    @TableField(value = "class_name")
    private String className;

    /**
     * 
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 
     */
    @TableField(value = "log_message")
    private String logMessage;

    /**
     * 
     */
    @TableField(value = "method_name")
    private String methodName;

    /**
     * 
     */
    @TableField(value = "params")
    private String params;

    /**
     * 
     */
    @TableField(value = "return_message")
    private String returnMessage;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}