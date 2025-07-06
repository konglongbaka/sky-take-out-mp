package com.sky.consumerOrListener;

import com.sky.entity.TbVoucherOrder;

public interface MQService {
    void sendMessage(TbVoucherOrder order);

    //发送消息
//
    void receiveMessage() throws InterruptedException;
}
