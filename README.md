# MarkLogicRestClient

Description - This project is a simple java based maven project that calls the /documents MarkLogic Rest Endpoint service in a loop with a uri of /image/{iterator}.json for the data it creates. The main class is configurable in order to specify host name, totalClusterHosts, and a configurable port. To change any of these settings, simply edit the variables below. 

The rest client was created in order to test MarkLogic system failover times, during a MarkLogic Node failure scenario. In order to test, make sure you specify a pattern in the serverName field below with the following format (HOSTNAME), where the program automatically appends a number after it in order to specify it's position in the cluster. As an example, this program uses vgsix-ml-ml as the hostname, and then 1,2,3 and so on up to the totalClusterHosts. That's why you'll see a final value of vgsix-ml-ml1, vgsix-ml-ml2, vgsix-ml-ml3, up to your totalClusterHosts. 

When you are ready to execute the program simply run it, and ensure that you hit the host initial timeout values set within your server. When you're ready, kill a MarkLogic node, and watch the failures begin. When failures start to occur, a round robin approach is used to search for any available nodes, until we find one that works after the forests are done failing over.
 
In order to track your fail over time, simply take the time elapsed value once the program resumes, and subtract from that the time elapsed value before the fail over starts to occur. 

# Host Timeout Settings - Caveats
Please keep in mind all of the documented MarkLogic caveats related to failover, when tuning xdqp/host timeout settings. It is never good to set the timeout too low, since it's possible that Nodes can be falsely voted out of the cluster due to bursty workload, slow network, etc. As always, make sure to read the MarkLogic documentation [XDQP Timeout, Host Timeout, and Host Initial Timeout Parameters](https://docs.marklogic.com/guide/cluster/config-both-failover#id_95009)
