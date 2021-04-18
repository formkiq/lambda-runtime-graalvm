# Lambda Runtime Graalvm Releases #

### Version 2.3.0 (April 18, 2021)
- [Pull 10](https://github.com/formkiq/lambda-runtime-graalvm/pull/10)
 * Fixed reflection bug when using GraalVM

### Version 2.2.2 (March 7, 2021)
- [Pull 9](https://github.com/formkiq/lambda-runtime-graalvm/pull/9)
 * Lambda handler class is instantiated for each request
 
### Version 2.2.1 (Jan 21, 2020)
- [Pull 7](https://github.com/formkiq/lambda-runtime-graalvm/pull/7)
 * renamed _X_AMZN_TRACE_ID to com.amazonaws.xray.traceHeader
 
### Version 2.2.0 (Jan 19, 2020)
- [Pull 6](https://github.com/formkiq/lambda-runtime-graalvm/pull/6)
 * Propagate the X-Ray tracing header by setting _X_AMZN_TRACE_ID (#6)

