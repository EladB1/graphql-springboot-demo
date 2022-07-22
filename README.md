GraphQL Spring Boot Demo
---

A small app to learn how to use GraphSQL With Spring Boot

## Examples

* Get book by id
  - Request:
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
  - Response:
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
    * Create Author
      - Request:
      ```graphql
      # mutation
      mutation CreateAuthor($id: ID!, $firstname: String!, $lastname: String!) {
        createAuthor(id: $id, firstName: $firstname, lastName: $lastname) {
          id,
          firstName,
          lastName
        }
      }
  
      #variables
      {
        "id": "author-1",
        "firstname": "Miguel",
        "lastname": "de Cervantes"
      }
      ```
      - Response:
      ```graphql
      {
        "data": {
            "createAuthor": {
            "id": "author-1",
            "firstName": "Miguel",
            "lastName": "de Cervantes"
            }
        }
      }
      ```

      * Create Book
        - Request:
        ```graphql
        # mutation
        mutation CreateBook($id: ID!, $name: String!, $pageCount: Int!, $authorID: ID!) {
          createBook(id: $id, name: $name, pageCount: $pageCount, authorID: $authorID) {
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
            "id": "book-1",
            "name": "Don Quixote",
            "pageCount": 285,
            "authorID": "author-1"
        }
        ```
        - Response:
        ```graphql
        {
            "data": {
                "createBook": {
                    "id": "book-1",
                    "name": "Don Quixote",
                    "pageCount": 285,
                    "author": {
                        "id": "author-1",
                        "firstName": "Miguel",
                        "lastName": "de Cervantes"
                    }
                }
            }
        }
        ```

