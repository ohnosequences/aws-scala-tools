# AWS Libraries

## Amazon AWS SDK

http://aws.amazon.com/sdkforjava/

### Pros

* Support, up-to-date

* Constains some undocumented signers in ```package com.amazonaws.auth```

```java
public interface Signer {
    public void sign(Request<?> request, AWSCredentials credentials) throws AmazonClientException;
}
```

### Cons

* Not really asynchronous: java futures (without get, map)