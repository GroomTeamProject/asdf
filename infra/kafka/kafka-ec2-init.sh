#!/bin/bash
set -e

# Docker 설치 (Ubuntu)
apt-get update -y
apt-get install -y docker.io docker-compose git curl

# Docker 서비스 시작
systemctl start docker
systemctl enable docker
usermod -aG docker ubuntu

# 권한 설정
chown ubuntu:ubuntu /home/ubuntu/docker-compose.yml

# Docker Compose 실행
cd /home/ubuntu
docker-compose up -d

# systemd 서비스 파일 이동
mv /home/ubuntu/kafka-compose.service /etc/systemd/system/ 2>/dev/null || true

# systemd 서비스 활성화
systemctl daemon-reload
systemctl enable kafka-compose.service

echo "Kafka installation completed!"

