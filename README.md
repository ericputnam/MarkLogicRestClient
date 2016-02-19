# MarkLogicRestClient

A simple REST based MarkLogic client that generates JSON insert requests, and logs elapsed time and system time. If a given MarkLogic node fails, the client attempts to use a round robin approach to find a new good client.

Future work may be needed to make the program a bit more intelligent in terms of selecting more nodes than three, in a failover scenario.
