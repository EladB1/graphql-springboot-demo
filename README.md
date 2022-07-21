GraphQL Spring Boot Demo
---

A small app to learn how to use GraphSQL With Spring Boot

## Example

*  Request: 
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

* Result:

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