This is a very **major** release. It introduces a lot of breaking changes and numerous improvements. Basically, _every_ part of the library was reviewed, refactored and/or rewritten from scratch. Update carefully.

----

The full list of changes would be too big and not very useful, but here are the highlights and links to pull-requests:

* Updated aws-sdk-java from `1.10.59` to `1.11.60`
* #40: Removed all code related to DynamoDB as outdated
* #39: Removed all code related to logging and benchmarking
* #38, #51: Restructured and cleaned up API for _all_ services.

  + #42: SQS
    * Added attributes get/set shortcuts and some useful read-only attributes as methods
    * Added _parallel batch_ message sending
    * Added queue polling (with messages number limit and/or timeout)

  + #43: SNS
    * Added different types of subscribers

  + #44: AutoScaling
    * Removed unnecessary AutoScalingGroup and LaunchConfiguration classes; redefined basic methods for them: get, create, delete
    * Added methods related to autoscaling tags
    * Removed all the price-evaluation complexity from the Spot purchase model
    * Removed dependency on EC2 client

  + #45: EC2
    * Added instance status ops (retrieval, state name, instance/system status summary shortcuts)
    * Instance API: added monitoring, reboot, userData, tags-related ops
    * Integrated `InstanceSpecs` into `LaunchSpecs` and improved it to have as a common type for different SDK requests (also in AutoScaling)
    * Added missing instance types
    * Changed instance types naming

  + #49: S3
    * Important fixes for the S3 addresses
    * Improved transfer progress listener and added `silent` parameter to the transfer methods (true by default)

  + #52: Regions
    * Added RegionAlias type
    * Added missing regions

  + #50, #53: AMI
    * Updated AMIs IDs to the Amazon Linux AMI `2016.09.0`
    * Added Amazon Linux AMIs for the missing regions

  Some general improvements in all APIs:

  * Removed any nested classes (S3, EC2, AutoScaling)
  * Almost all methods now return `Try`
  * Any listing methods with pagination will now automatically rotate the token and return a `Stream` of results
  * Unified clients interface and constructors
  * Waiters API for EC2 and AutoScaling
