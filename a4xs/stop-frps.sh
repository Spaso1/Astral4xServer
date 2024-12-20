#!/bin/bash

# 查找占用 7000 端口的进程 ID
PID=$(netstat -apn | grep ':7000' | awk '{print $7}' | cut -d'/' -f1)

if [ -z "$PID" ]; then
    echo "没有找到占用 7000 端口的进程"
else
    echo "找到占用 7000 端口的进程 ID: $PID"
    # 终止进程
    kill -9 $PID
    if [ $? -eq 0 ]; then
        echo "成功终止进程 ID: $PID"
    else
        echo "终止进程 ID: $PID 失败"
    fi
fi
