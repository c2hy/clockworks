# clockworks

A lightweight timer service suitable for distributed environments.

## API Design

The API of this service follows the Restful API style.


### The API General Specification Explanation

The HTTP methods used include:

- POST is used for creating new resources or batch creation, also for performing actions on a resource.
- DELETE is used for deleting individual or batch resources.
- PUT is used for updating individual or batch resources.
- GET is used for querying specific or a batch of resources.


The HTTP response results may have the following scenarios:
- 200 indicates that the request has been successfully processed. If it's a query request, the response will also include the query results. It's important to note that if the specific resource queried doesn't exist, the request result will be empty.
- 202 indicates that except for GET requests, other requests may return this status code when receiving batch requests. It indicates that the request has been received but is being processed asynchronously. The response header will include a Location to inform how to query the results of the asynchronous execution.
- 400 indicates that the request parameter structure is correct, but the parameters do not meet the requirements and cannot be processed normally.
- 401 indicates that when accessing an undisclosed API, no authentication header is provided, or an invalid authentication header is provided.
- 403 indicates that when accessing an undisclosed API, even though a valid authentication header is provided, the user identified by the authentication header does not have permission to perform the operation.
- 500 indicates that an unexpected exception occurred during request processing.
- 503 indicates that an unexpected exception occurred during request processing. However, if the Retry-After header is returned in the response header and its value is 1, the request can be retried. 

Other status codes not listed but possibly returned generally have their usual meanings.