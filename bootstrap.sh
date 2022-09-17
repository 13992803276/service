# 开始启动服务
cd detect/
# 启动检测服务
nohup python3 detect_service.py > detect.out 2>&1 &
nohup python3 segment_service.py > segment.out 2>&1 &
cd ../scripts/
nohup python3 quantify_service.py > quantify.out 2>&1 &