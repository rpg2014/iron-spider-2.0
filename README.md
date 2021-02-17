## Factorio expansion
boot up random ec2, that on boot (though userdata, fetches an install script (from github) 

This install script fetches the factorio server, and the save file from s3(filename stored in dynamo? or is constant in s3)
and launches the server

can use RunCommand java api to run wget to fetch server, and can do same with save file, getting a secure code.
then we can map the dns


## Current todo
have a server settings json file in github that the server pulls.  