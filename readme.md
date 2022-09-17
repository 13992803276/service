文件夹中用于提供以下服务：

1. java后端的服务，文件server.jar
2. 裂缝的检测服务，文件/detect/detect_service.py
3. 面积型的检测服务，文件/detect/segment_service.py
4. 损伤量化服务，文件/scripts/quantify_service.py

全部服务的启动可以通过

```bash
./bootstrap.sh
```

进行启动

下面是整个服务的启动步骤

1. 跟随容器启动mysql8.0
2. 后台启动redis
3. 启动python编写的三个服务
4. 启动java编写的后端服务