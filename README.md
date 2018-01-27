# libnetrc
The library for reading and writing of .netrc files. It exposes API for parsing of .netrc like:

```
machine shard1.cloud.databricks.com
login user.name@gmail.com
password 12345678

machine collab.cloud.com login anonymous password helloworld

default login anonymous password 123 account 456
```

and returns a collection of Scala classes that reflect the file structure:

```
NetRc(Seq(
    Machine(name = "shard1.cloud.databricks.com", login = "user.name@gmail.com", password = "12345678"),
    Machine(name = "collab.cloud.com", login = "anonymous", password = "helloworld"),
    Default(login = "anonymous", password = "123", account = Some("456"))
))
```

By default, the representation can be get from default .netrc in the home directory:

```
val netrc = NetRcFile.read 
```

In Linux, the file is /home/username/.netrc, on Mac OS it is /Users/username/.netrc and on Windows the name of the file is _netrc in the home dir.

Once the file is read and parsed, it could be updated by new items:

```
val netNetRc = netrc.upsert(
    Machine("shard1.cloud.databricks.com", "user@outlook.com", "qwerty")
)
```

The upsert method looks for an item with the same name and update it if it exists otherwise it adds new one. New machine items is added before any default items. New default item updates the old one if it is presented in the structure or replaces all existed default items. In any case, only one default item presents after the update.

The delete method finds all machine items with the matched names and deletes thems:

```
netrc.delete("*.cloud.com")
```

To delete all default items:

```
netrc.deleteDefault()
```

The find method scans all items and checks either item's name, login, password or account is matched to user's needs:

```
val items: List[Items] = netrc.find(name = Some("*.com"), login = Some("user.name@gmail.com"))
```

The updated instance of NetRc could be saved back to the .netrc file:

```
newNetRc.save("/home/username/copy_of_netrc")
```

besides of that it could be appended to an existing file:

```
NetRc(Seq(
    Machine("host1", "user1", "password1")
)).save(append = true)
```