# Code Audit Action Items Checklist

This checklist provides actionable tasks derived from the comprehensive code audit. Check off items as they are completed.

---

## 🚨 PHASE 1: CRITICAL SECURITY FIXES (WEEK 1-2)
**⚠️ DEPLOYMENT BLOCKED UNTIL COMPLETE ⚠️**

### P0-1: API Key Security
- [ ] Create backend API proxy service
- [ ] Move API key logic to backend
- [ ] Remove `BuildConfig.AIML_API_KEY` from `app/build.gradle.kts`
- [ ] Remove `BuildConfig.KINDWISE_API_KEY` from `app/build.gradle.kts`
- [ ] Remove `BuildConfig.PERENUAL_API_KEY` from `app/build.gradle.kts`
- [ ] Update all API services to call backend proxy instead
- [ ] Test API calls through proxy
- [ ] Verify keys not present in decompiled APK

**Files to modify:**
- `app/build.gradle.kts`
- `data/remote/api/ai/aiml/AimlRetrofitInstance.kt`
- `data/remote/api/ai/kindwise/KindwisePlantIdRetrofitInstance.kt`
- `data/remote/api/plant/PerenualApi.kt`

---

### P0-2: Force-Unwrap Safety
- [ ] Fix `FirebaseAuthService.kt` line 46: Replace `currentUser!!` with null check
- [ ] Search codebase for other `!!` operators
- [ ] Replace all `!!` with safe calls or proper null handling
- [ ] Add unit tests for null user scenarios

**Files to modify:**
- `data/service/auth/FirebaseAuthService.kt`

---

### P0-3: Permission Handling
- [ ] Update `MyFirebaseMessagingService.kt` lines 65-78
- [ ] Implement `storeNotificationForLater()` function
- [ ] Add warning log for missing permission
- [ ] Create in-app notification display for stored notifications
- [ ] Test notification flow without POST_NOTIFICATIONS permission

**Files to modify:**
- `data/service/firebase/messaging/MyFirebaseMessagingService.kt`

---

### P0-4: Network Security Configuration
- [ ] Create `app/src/main/res/xml/network_security_config.xml`
- [ ] Add base config to disallow cleartext traffic
- [ ] Configure domain-specific rules for APIs
- [ ] Add `android:networkSecurityConfig` to `AndroidManifest.xml`
- [ ] Test HTTPS enforcement
- [ ] Consider certificate pinning for production

**Files to create:**
- `app/src/main/res/xml/network_security_config.xml`

**Files to modify:**
- `app/src/main/AndroidManifest.xml`

---

### P1-5: ProGuard Rules
- [ ] Open `app/proguard-rules.pro`
- [ ] Add rules for data models
- [ ] Add rules for Retrofit
- [ ] Add rules for Firebase
- [ ] Add rules to remove Log statements in release
- [ ] Enable minification in `build.gradle.kts`
- [ ] Test release build
- [ ] Verify obfuscation with APK decompiler

**Files to modify:**
- `app/proguard-rules.pro`
- `app/build.gradle.kts`

---

## 🔥 PHASE 2: STABILITY IMPROVEMENTS (WEEK 3-5)

### P1-6: Exception Handling
- [ ] Review all `catch (e: Exception)` blocks
- [ ] Replace with specific exception types
- [ ] Add proper error logging
- [ ] Test error scenarios

**Files to modify:**
- `feature/auth/login/LoginScreenViewModel.kt`
- `data/repositories/plant/PlantRepositoryImpl.kt`
- `feature/home/user_plants/details/UserPlantDetailsViewModel.kt`
- All ViewModels and Repositories

---

### P1-7: Immutable Data Classes
- [ ] Change `UserPlant.id` from `var` to `val`
- [ ] Change `UserPlant.isFavourite` from `var` to `val`
- [ ] Update all mutation code to use `copy()`
- [ ] Update `UserPlantDetailsViewModel` favorite logic (lines 182-184)
- [ ] Update `UserPlantDetailsViewModel` water logic (lines 195-196)
- [ ] Test plant updates work correctly

**Files to modify:**
- `domain/model/plant/user/UserPlant.kt`
- `feature/home/user_plants/details/UserPlantDetailsViewModel.kt`

---

### P1-8: Network Timeouts
- [ ] Add timeout constants to each RetrofitInstance
- [ ] Configure `connectTimeout(30, TimeUnit.SECONDS)`
- [ ] Configure `readTimeout(60, TimeUnit.SECONDS)`
- [ ] Configure `writeTimeout(30, TimeUnit.SECONDS)`
- [ ] Test timeout behavior

**Files to modify:**
- `data/remote/api/ai/aiml/AimlRetrofitInstance.kt`
- `data/remote/api/plant/PerenualRetrofitInstance.kt`
- `data/remote/api/ai/kindwise/KindwisePlantIdRetrofitInstance.kt`

---

### P1-9: Retry Logic
- [ ] Create `RetryInterceptor` class
- [ ] Implement retry logic with exponential backoff
- [ ] Add to all OkHttpClient instances
- [ ] Test retry behavior
- [ ] Configure max retries (3 recommended)

**Files to create:**
- `data/remote/interceptor/RetryInterceptor.kt`

**Files to modify:**
- All RetrofitInstance files

---

### P1-10: Auth State Cleanup
- [ ] Verify `awaitClose` properly removes listener in `FirebaseAuthService.kt`
- [ ] Add `.flowOn(Dispatchers.IO)` to flow
- [ ] Test for memory leaks
- [ ] Monitor listener count in logs

**Files to modify:**
- `data/service/auth/FirebaseAuthService.kt`

---

### P1-11: Input Validation
- [ ] Create `PlantDataValidator` class
- [ ] Add validation for plant name (not blank)
- [ ] Add validation for watering period (> 0)
- [ ] Add validation for dimensions (>= 0, min <= max)
- [ ] Update `UserPlantDetailsViewModel.saveModifications()` with validation
- [ ] Add validation error messages to strings.xml
- [ ] Test invalid inputs

**Files to create:**
- `domain/validation/PlantDataValidator.kt`

**Files to modify:**
- `feature/home/user_plants/details/UserPlantDetailsViewModel.kt`

---

### P1-12: Image Upload Error Handling
- [ ] Update `saveModifications()` to notify on upload failure
- [ ] Add user-facing error message
- [ ] Log upload failures
- [ ] Test image upload failure scenario

**Files to modify:**
- `feature/home/user_plants/details/UserPlantDetailsViewModel.kt` (lines 136-141)

---

### P1-13: URI Validation
- [ ] Wrap `Uri.parse()` in try-catch
- [ ] Handle parsing exceptions
- [ ] Add user error message
- [ ] Test with invalid URI strings

**Files to modify:**
- `feature/home/user_plants/details/UserPlantDetailsViewModel.kt` (line 136)

---

### P1-14: Firestore Security Documentation
- [ ] Create `FIRESTORE_SECURITY_RULES.md`
- [ ] Document required security rules
- [ ] Add deployment instructions
- [ ] Link from main README.md

**Files to create:**
- `FIRESTORE_SECURITY_RULES.md`

---

### P1-15: HTTP Logging
- [ ] Add OkHttp logging interceptor dependency
- [ ] Add interceptor to debug builds only
- [ ] Configure logging level (BODY for debug)
- [ ] Test logging output

**Files to modify:**
- All RetrofitInstance files

---

### P1-16: Crashlytics Integration
- [ ] Add `setUserId()` calls in ViewModels
- [ ] Add `setCustomKey()` for context
- [ ] Add `recordException()` in catch blocks
- [ ] Test crash reporting

**Files to modify:**
- All ViewModels

---

## 🟡 PHASE 3: PERFORMANCE & UX (WEEK 6-9)

### P2-17: Remove Unused Code
- [ ] Delete `Greeting` composable from `MainActivity.kt` (lines 64-78)
- [ ] Search for other unused functions
- [ ] Remove unused imports

**Files to modify:**
- `MainActivity.kt`

---

### P2-18: Consistent Error Handling
- [ ] Review all error handling in ViewModels
- [ ] Ensure `return@launch` after error events
- [ ] Standardize error handling pattern
- [ ] Document pattern in code style guide

**Files to modify:**
- `feature/auth/login/LoginScreenViewModel.kt`
- All ViewModels

---

### P2-19: Success Event Management
- [ ] Review `UiEvent.Success` usage
- [ ] Remove redundant success events
- [ ] Only send on user action completion
- [ ] Test UI state management

**Files to modify:**
- `feature/home/user_plants/details/UserPlantDetailsViewModel.kt`

---

### P2-20: Production Logging
- [ ] Add Timber dependency
- [ ] Initialize Timber in debug builds only
- [ ] Replace all `Log.d` with `Timber.d`
- [ ] Replace all `Log.e` with `Timber.e`
- [ ] Add ProGuard rule to remove logs

**Files to modify:**
- `PlantBuddyApplication.kt`
- All files with Log statements

---

### P2-21: Coroutine Cancellation
- [ ] Add `isActive` checks in long-running operations
- [ ] Use `withContext` for IO operations
- [ ] Test cancellation behavior

**Files to modify:**
- All ViewModels with long operations

---

### P2-22: String Resources
- [ ] Extract all hardcoded strings
- [ ] Add to `strings.xml`
- [ ] Update code to use string resources
- [ ] Prepare for localization

**Files to modify:**
- `feature/home/user_plants/details/UserPlantDetailsViewModel.kt`
- `app/src/main/res/values/strings.xml`

---

### P2-23: Firestore Transactions
- [ ] Update `saveFcmToken()` to use transactions
- [ ] Update `saveFavouritePlant()` to use transactions
- [ ] Test transaction rollback

**Files to modify:**
- `data/repositories/firebase/FirestoreRepositoryImpl.kt`

---

### P2-24: Pagination Reset
- [ ] Add pagination reset on filter change
- [ ] Clear existing data on filter change
- [ ] Test filter + pagination interaction

**Files to modify:**
- API plant list ViewModels

---

### P2-25: Password Validation
- [ ] Create `PasswordStrengthUseCase`
- [ ] Add password requirements (8+ chars, upper, lower, digit, special)
- [ ] Update registration flow
- [ ] Add user feedback for password strength
- [ ] Test password validation

**Files to create:**
- `domain/usecases/PasswordStrengthUseCase.kt`

**Files to modify:**
- `feature/auth/register/RegisterScreenViewModel.kt`

---

### P2-26: DI Refactoring
- [ ] Remove RetrofitInstance objects
- [ ] Create provider functions in ApiModule
- [ ] Inject OkHttpClient
- [ ] Test dependency injection

**Files to modify:**
- `data/di/ApiModule.kt`
- All RetrofitInstance files

---

### P2-27: Loading Timeouts
- [ ] Add `withTimeout()` to all loading operations
- [ ] Set appropriate timeout values (30s recommended)
- [ ] Handle `TimeoutCancellationException`
- [ ] Test timeout behavior

**Files to modify:**
- All ViewModels with loading states

---

### P2-28: Code Cleanup
- [ ] Remove unused import from `FirebaseStorageServiceImpl.kt` (line 8)
- [ ] Run code cleanup in IDE
- [ ] Fix all warnings

**Files to modify:**
- `data/service/firebase/storage/FirebaseStorageServiceImpl.kt`

---

### P2-29: Response Caching
- [ ] Add Cache to OkHttpClient
- [ ] Configure cache size (10 MB)
- [ ] Add cache-control headers
- [ ] Test offline behavior

**Files to create:**
- `data/di/CacheModule.kt`

---

### P2-30: Test Coverage
- [ ] Write tests for image upload validation
- [ ] Write tests for concurrent plant updates
- [ ] Write integration tests for API
- [ ] Write UI tests for critical flows
- [ ] Target: 60% coverage

---

### P2-31: Analytics
- [ ] Add Firebase Analytics event logging
- [ ] Track plant_saved event
- [ ] Track ai_query_completed event
- [ ] Track user_login event
- [ ] Test analytics dashboard

**Files to modify:**
- All ViewModels

---

### P2-32: Memory Management
- [ ] Replace `mutableStateOf` with `MutableStateFlow`
- [ ] Update collectors
- [ ] Test for memory leaks
- [ ] Profile memory usage

**Files to modify:**
- `feature/home/user_plants/details/UserPlantDetailsViewModel.kt`

---

### P2-33: Conflict Resolution
- [ ] Implement optimistic locking for plant updates
- [ ] Fetch latest before save
- [ ] Show conflict dialog on version mismatch
- [ ] Test concurrent updates

**Files to modify:**
- `feature/home/user_plants/details/UserPlantDetailsViewModel.kt`

---

### P2-34: Null Safety
- [ ] Add null check to `getPlantById`
- [ ] Handle missing documents
- [ ] Add logging for errors
- [ ] Test non-existent document retrieval

**Files to modify:**
- `data/repositories/firebase/FirestoreRepositoryImpl.kt`

---

## 🟢 PHASE 4: POLISH & MAINTAINABILITY (WEEK 10-12)

### P3-35: Naming Consistency
- [ ] Fix `perPager` → `perPage` in `PerenualApi.kt`
- [ ] Review all naming conventions
- [ ] Update to follow Kotlin conventions

**Files to modify:**
- `data/remote/api/plant/PerenualApi.kt`

---

### P3-36: Dispatcher Standardization
- [ ] Review all ViewModel launch blocks
- [ ] Standardize on `Dispatchers.IO` for network/DB
- [ ] Standardize on `Dispatchers.Main` for UI
- [ ] Document in code style guide

**Files to modify:**
- All ViewModels

---

### P3-37: Extract Constants
- [ ] Extract magic numbers to constants
- [ ] Create companion objects
- [ ] Document constant meanings

**Files to modify:**
- `feature/home/user_plants/details/UserPlantDetailsViewModel.kt`

---

### P3-38: Documentation
- [ ] Add KDoc to all public functions
- [ ] Document complex algorithms
- [ ] Add class-level documentation
- [ ] Generate documentation

**Files to modify:**
- All ViewModels and Repositories

---

### P3-39: Nullable Consistency
- [ ] Review nullable properties in data classes
- [ ] Standardize nullable patterns
- [ ] Add default values where appropriate

**Files to modify:**
- `domain/model/plant/user/UserPlant.kt`

---

### P3-40: Fix Typos
- [ ] Fix "FMC" → "FCM" in `MyFirebaseMessagingService.kt` line 26
- [ ] Run spell check on all files

**Files to modify:**
- `data/service/firebase/messaging/MyFirebaseMessagingService.kt`

---

### P3-41: .gitignore Update
- [ ] Add `google-services.json` to .gitignore
- [ ] Verify no secrets in repository
- [ ] Update documentation about required files

**Files to modify:**
- `.gitignore`

---

### P3-42: Version Display
- [ ] Add version display to ProfileScreen
- [ ] Show version name and code
- [ ] Test version display

**Files to modify:**
- `feature/home/profile/ProfileScreen.kt`

---

## 📊 Progress Tracking

### By Phase
- [ ] Phase 1 Complete (0/5 issues)
- [ ] Phase 2 Complete (0/12 issues)
- [ ] Phase 3 Complete (0/18 issues)
- [ ] Phase 4 Complete (0/8 issues)

### By Priority
- [ ] P0 Issues (0/4)
- [ ] P1 Issues (0/12)
- [ ] P2 Issues (0/18)
- [ ] P3 Issues (0/8)

### Overall Progress: 0/42 (0%)

---

## 🎯 Success Criteria

### Phase 1 Done When:
- ✅ All P0 issues resolved
- ✅ Security scan passes
- ✅ API keys not in APK
- ✅ Code review approved

### Phase 2 Done When:
- ✅ All P1 issues resolved
- ✅ Stability tests pass
- ✅ No force-unwrap operators
- ✅ All inputs validated

### Phase 3 Done When:
- ✅ Test coverage > 60%
- ✅ Performance benchmarks met
- ✅ Caching implemented
- ✅ Analytics working

### Phase 4 Done When:
- ✅ All documentation complete
- ✅ Code style consistent
- ✅ All warnings resolved
- ✅ Ready for production

---

**Last Updated:** December 7, 2024
