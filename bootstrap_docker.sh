# 环境变量
source /etc/profile
# 启动redis
cd /home/
redis-server redis.conf
# 将表结构导入到mysql中
cd services/
mysql -uroot -p123456 < tables.sql
# 开始启动服务
cd detect/
# 启动检测服务
nohup python3 detect_service.py > detect.out 2>&1 &
nohup python3 segment_service.py > segment.out 2>&1 &
cd ../scripts/
nohup python3 quantify_service.py > quantify.out 2>&1 &
# 启动java后端服务
cd ../
nohup java -jar server.jar > server.out 2>&1 &