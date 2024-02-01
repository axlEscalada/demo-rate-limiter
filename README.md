# Rate Limiter And Notification Service

## Contents

[TOC]

## Architecture

In order to implement a Notification service with a rate limiter I decided to use one component for notification and rate limiter for simplicity. This component is an API rest that each request that receive for notification it checks that is candidate to se sent or if it's dropped of the message service and return a clear message of the error.

## Diagram 
![image](https://github.com/axlEscalada/demo-rate-limiter/assets/87334103/8aa69877-0462-4c44-95ca-2246edba27ff)


### Benefits of this design
It's a single component due to the simplicity of the case, it's possible to scale out the component and also scale the in-memory db (redis).


### Rate limiter algorithm
The algorithm used in this rate limiter is sliding window

## TODOs/Improvements:
- Define a clear retry strategy for dropped notifications, for example instead of drop request that exceeds rate limit treshold you could add them to a queue.
- Metrics of sent notifications and possible failures
- Race condition while reading stored values in Redis, a solution could be implement a [distributed lock](https://redis.com/ebook/part-2-core-concepts/chapter-6-application-components-in-redis/6-2-distributed-locking/).
- Create a interface for data access, for interchangeability of implementations if we don't want to use redis in a future.

