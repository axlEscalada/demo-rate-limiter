# Rate Limiter And Notification Service
This is a single service that take request from many sources check a rate limit based on a Domain Type and let send notifications or drop request based on that check.

## Architecture

In order to implement a Notification service with a rate limiter I decided to use one component for notification and rate limiter for simplicity. This component is an API rest that each request that receive for notification it checks that is candidate to se sent or if it's dropped of the message service and return a clear message of the error.

## Diagram 
![image](https://github.com/axlEscalada/demo-rate-limiter/assets/87334103/793c92e1-94e9-4a26-a968-a524a6d689b7)


### Benefits of this design
It's a single component due to the simplicity of the case, it's possible to scale out the component and also scale the in-memory db (redis).

### Disadvantages 
If there is a lot of messages drop due to rate limiter the whole service is going to scale out and probably also scale the notification service without the necessity of do it, a good solution for this case scenario is extract the rate limiter service and make it as a separate component that interact as a middleware between source of notifications and notifification service.

### Rate limiter algorithm
The algorithm used in this rate limiter is fixed window counter, this algorithm will store the first notification given a key (domain plus an address) and store the number of ocurrences in redis setting a ttl using the domain configuration. For example a domaing configuration look like this:
```
limits:
  configMap:
    status:
      unit: MINUTES
      rate: 1
      limit: 2
```
Being the rate the quantity of time, unit the unit of time (seconds, minutes, hours) and the limit the quantity of notifications allowed in this range.
So in this case the first message will be stored with a TTL of 1 minute, here is where the window of time starts. In the next minute the rate limiter will allow an extra message til it reach the limit of 2, when the second message reach the rate limiter service it will use the same key and update the value by 1 and keeping the TTL of the first message.
Let's say that:
- first message enter 10:01:01 so the TTL will be 60 seconds
- second message enter 10:01:41 the TTL that will be keept is 20 seconds because is whats left to drop the first message

## TODOs/Improvements:
- Write documentation of API
- Define a clear retry strategy for dropped notifications, for example instead of drop request that exceeds rate limit treshold it could add them to a queue and reprocess.
- Metrics of sent notifications and possible failures
- Create a interface for data access, for interchangeability of implementations if we don't want to use redis in a future.

## Known issues
### Race condition
If a race condition happen when a value is read and the rate limit is close to be reached the service could send more notifications than desired. A good approach to solve this is use a [distributed lock](https://redis.com/ebook/part-2-core-concepts/chapter-6-application-components-in-redis/6-2-distributed-locking/) but this problably will make the service slower.

