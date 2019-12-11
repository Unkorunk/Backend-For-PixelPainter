# Backend for Pixel Painter

## API consists from groups:
1. account
1. gallery

### Account consists from methods:
#### 1. login
##### Input(GET): 
1. login : string (required) (max length = 32)
1. password(non-hashed) : string (required) (min length = 6)
##### Output: 
1. status(OK, FAIL) : string 
1. token : string (if OK return string length = 32 else empty string)
##### Example:
```address_server/account/login?login=some_login&password=some_password```
#### 2. register
##### Input(GET): 
1. login : string (required) (max length = 32)
1. password(non-hashed) : string (required) (min length = 6)
##### Output:
1. status(OK, FAIL) : string
##### Example:
```address_server/account/register?login=some_login&password=some_password```

### Gallery consists from methods:
#### 1. get
##### Input(GET):
1. offset : integer
1. count : integer
1. token : string (length = 32)
##### Output:
1. status(OK, FAIL) : string
1. items : array of Art

Art = {art_id : integer, data : string, owner: bool}
owner = (owner picture == owner token)
##### Examples:
```address_server/gallery/get```

```address_server/gallery/get?token=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx```

```address_server/gallery/get?offset=0&count=2&token=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx```
#### 2. create
##### Input(GET):
1. data : string (required)
1. is_private : bool (required)
1. token : string (required) (length = 32)
##### Output:
1. status(OK, FAIL) : string
1. art_id : integer
##### Example: 
```address_server/gallery/create?data=data:image/png;base64,...&is_private=1&token=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx```
#### 3. edit
##### Input(GET):
1. art_id : integer
1. data : string (required)
1. token : string (required) (length = 32)
##### Output:
1. status(OK, FAIL) : string
##### Example:
```address_server/gallery/edit?art_id=123&data=data:image/png;base64,...&token=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx```