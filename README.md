# PoorManTasks

This is non-prod alpha stuff - don't use it :)

# Sample

```
TASK_ID=$(curl -s -d '{"task":{"callbackUrl":"http://foo", "fields":[{"name":"foo","label":"Foo","value":"default","type":"INPUT"}]}}' -H"Content-Type:application/json" https://pmt.furthermore.ch/tasks | jq -r ".taskId")
echo Open in browser: https://pmt.furthermore.ch/pending/$TASK_ID
```
