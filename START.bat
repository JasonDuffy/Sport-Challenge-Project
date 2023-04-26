:: Backend Build
cd Backend
call mvn clean package -Dmaven.test.skip=true
docker build . -t scp-backend
cd ..

:: Frontend Build
cd Frontend
docker build . -t scp-frontend

:: Start Docker
docker compose up