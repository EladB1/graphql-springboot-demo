GraphQL Spring Boot Demo
---

A small app to learn how to use GraphSQL With Spring Boot

## Examples

* Get book by id 
  
   Request: 
```graphql
    # query
    query GetBooks($id: ID!) {
        bookById(id: $id) {
            id,
            name,
            pageCount,
            author {
                id,
                firstName,
                lastName
            }
        }
    }

    # variables
    {
        "id": "book-1"
    }

```

   Result:

```graphql
    {
      "data": {
        "bookById": {
          "id": "book-1",
          "name": "1984",
          "pageCount": 206,
          "author": {
            "id": "author-1",
            "firstName": "George",
            "lastName": "Orwell"
          }
        }
      }
    }
```

* Get all books:
  - Request:
  ```graphql
  query allBooks {
    allBooks {
      name,
      pageCount,
      author {
        id,
        firstName,
        lastName
      }
    }
  }
  ```
  - Response:
  ```graphql
    {
        "data": {
        "allBooks": [
            {
                "name": "1984",
                "pageCount": 206,
                "author": {
                    "id": "author-1",
                    "firstName": "George",
                    "lastName": "Orwell"
                }
            },
            {
                "name": "Fahrenheit 451",
                "pageCount": 317,
                "author": {
                    "id": "author-2",
                    "firstName": "Ray",
                    "lastName": "Bradbury"
                }
            },
            {
                "name": "Frankenstein",
                "pageCount": 199,
                "author": {
                    "id": "author-3",
                    "firstName": "Mary",
                    "lastName": "Shelley"
                }
            }
        ]
    }
  }
  ```