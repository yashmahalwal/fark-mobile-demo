# Fark Mobile Demo (Kotlin/Android)

Android mobile application that uses REST, GraphQL, and gRPC APIs from the backend. Built for testing Fark.ai's frontend impact detection in mobile apps.

## Tech Stack

- **Kotlin** for Android development
- **Retrofit** for REST API
- **Apollo GraphQL** for GraphQL API
- **gRPC** for gRPC API
- **Coroutines** for async operations
- **View Binding** for UI

## Project Structure

```
app/src/main/
├── java/com/fark/mobiledemo/
│   ├── MainActivity.kt              # Main UI activity
│   ├── models/
│   │   └── Models.kt                 # Data models (User, Order, Product)
│   └── api/
│       ├── rest/
│       │   ├── RestApi.kt           # REST API interface
│       │   └── RestApiClient.kt    # REST API client implementation
│       ├── graphql/
│       │   └── GraphQLClient.kt    # GraphQL client
│       └── grpc/
│           └── GrpcClient.kt        # gRPC client
└── res/
    ├── layout/
    │   └── activity_main.xml        # Main UI layout
    └── values/
        ├── strings.xml
        ├── colors.xml
        └── themes.xml
```

## Setup

1. **Install Android Studio** with Android SDK
2. **Open project** in Android Studio
3. **Sync Gradle** files
4. **Run** on emulator or device

## API Usage

### REST API
- **Client**: `RestApiClient.kt`
- **Interface**: `RestApiService.kt`
- **Endpoints**: `/api/users/:id`, `/api/orders/:id`, `/api/products`
- **Fields referenced**: `email`, `name`, `status`, `description`, `metadata`, `tags`, `paymentMethod`

### GraphQL API
- **Client**: `GraphQLClient.kt`
- **Queries**: `GetUserQuery`, `GetUsersQuery`, `GetOrderQuery`, `GetProductsQuery`
- **Mutations**: `CreateUserMutation`
- **Fields referenced**: All User/Order/Product fields

### gRPC API
- **Client**: `GrpcClient.kt`
- **Methods**: `getUser()`, `createUser()`, `listUsers()`
- **Fields referenced**: `email`, `description`, `tags`, `payment_method`, `page`, `page_size`
- **Note**: Proto files need to be generated for full functionality

## Features

The mobile app uses all three API interfaces and references:
- **User fields**: `email`, `name`, `status`, `description`, `metadata`, `tags`, `paymentMethod`
- **Order fields**: `userId`, `productIds`, `status`, `total`, `discountCode`, `shippingAddress`
- **Product fields**: `name`, `price`, `category`, `inStock`, `specifications`
- **Enums**: `UserStatus`, `OrderStatus`, `PaymentMethod`
- **gRPC fields**: `payment_method`, `page_size`

## Testing with Fark.ai

When the backend introduces breaking changes (e.g., `email` → `emailAddress`), Fark.ai's frontend impact finder will:
1. Search this codebase for `email` references
2. Find them in: `RestApiClient.kt`, `GraphQLClient.kt`, `GrpcClient.kt`, `MainActivity.kt`
3. Report impacted files with line numbers and severity
4. Suggest fixes for Kotlin/Android code

## Network Configuration

- **Emulator**: Uses `10.0.2.2` to access host `localhost`
- **REST/GraphQL**: `http://10.0.2.2:3000`
- **gRPC**: `10.0.2.2:50051`
- **Cleartext traffic**: Enabled for development (see `AndroidManifest.xml`)

## Building

```bash
./gradlew build
./gradlew assembleDebug
./gradlew installDebug
```

## Notes

- The backend must be running on `localhost:3000` (REST/GraphQL) and `localhost:50051` (gRPC)
- gRPC proto files need to be generated for full functionality
- All models match the backend API structure
- Uses Kotlin coroutines for async operations
- View Binding is enabled for type-safe view access




