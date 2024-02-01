
build:
	gradle clean
	gradle build -x test
	docker build -t rate-limiter-service .

run-app:
	docker-compose up
