package com.sky.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @TableName tb_voucher
 */
@TableName(value = "tb_voucher")
@Data
public class TbVoucher implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商铺id
     */
    @TableField(value = "shop_id")
    private Long shopId;

    /**
     * 代金券标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 副标题
     */
    @TableField(value = "sub_title")
    private String subTitle;

    /**
     * 使用规则
     */
    @TableField(value = "rules")
    private String rules;

    /**
     * 支付金额，单位是分。例如200代表2元
     */
    @TableField(value = "pay_value")
    private Long payValue;

    /**
     * 抵扣金额，单位是分。例如200代表2元
     */
    @TableField(value = "actual_value")
    private Long actualValue;

    /**
     * 0,普通券；1,秒杀券
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 1,上架; 2,下架; 3,过期
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        TbVoucher other = (TbVoucher) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getShopId() == null ? other.getShopId() == null : this.getShopId().equals(other.getShopId()))
                && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
                && (this.getSubTitle() == null ? other.getSubTitle() == null : this.getSubTitle().equals(other.getSubTitle()))
                && (this.getRules() == null ? other.getRules() == null : this.getRules().equals(other.getRules()))
                && (this.getPayValue() == null ? other.getPayValue() == null : this.getPayValue().equals(other.getPayValue()))
                && (this.getActualValue() == null ? other.getActualValue() == null : this.getActualValue().equals(other.getActualValue()))
                && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
                && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
                && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
                && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getShopId() == null) ? 0 : getShopId().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getSubTitle() == null) ? 0 : getSubTitle().hashCode());
        result = prime * result + ((getRules() == null) ? 0 : getRules().hashCode());
        result = prime * result + ((getPayValue() == null) ? 0 : getPayValue().hashCode());
        result = prime * result + ((getActualValue() == null) ? 0 : getActualValue().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", shopId=").append(shopId);
        sb.append(", title=").append(title);
        sb.append(", subTitle=").append(subTitle);
        sb.append(", rules=").append(rules);
        sb.append(", payValue=").append(payValue);
        sb.append(", actualValue=").append(actualValue);
        sb.append(", type=").append(type);
        sb.append(", status=").append(status);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}