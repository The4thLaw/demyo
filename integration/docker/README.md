# Docker integration

## Prepare

Create a `.env` file based on the `.env.example` file.

## Build

```
docker-compose build
```

## Run

```
docker run --name demyo -p 8080:8080 -v demyo-data:/demyo-data -d --rm demyo:3.1.0
```

or

```
docker-compose up -d
```
