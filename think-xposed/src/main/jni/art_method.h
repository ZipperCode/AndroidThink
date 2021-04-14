/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#ifndef ART_RUNTIME_ART_METHOD_H_
#define ART_RUNTIME_ART_METHOD_H_
#include <unistd.h>
#include <cstdio>
#include <cstddef>

#define FINAL final

#define OFFSETOF_MEMBER(t, f) \
  (reinterpret_cast<uintptr_t>(&reinterpret_cast<t*>(16)->f) - static_cast<uintptr_t>(16u))  // NOLINT

#ifdef NDEBUG
#define DECLARE_RUNTIME_DEBUG_FLAG(x) \
  static constexpr bool x = false;
#define DEFINE_RUNTIME_DEBUG_FLAG(C, x) \
  static_assert(!C::x, "Unexpected enabled flag in release build");
#else
#define DECLARE_RUNTIME_DEBUG_FLAG(x) \
  static bool x;
#define DEFINE_RUNTIME_DEBUG_FLAG(C, x) \
  bool C::x = RegisterRuntimeDebugFlag(&C::x);
#endif  // NDEBUG
bool RegisterRuntimeDebugFlag(bool* runtime_debug_flag);
void SetRuntimeDebugFlagsEnabled(bool enabled);

namespace art {

template<class T> class Handle;
class ImtConflictTable;
enum InvokeType : uint32_t;
union JValue;
class OatQuickMethodHeader;
class ProfilingInfo;
class ScopedObjectAccessAlreadyRunnable;
class StringPiece;
class ShadowFrame;


namespace mirror {
class Array;
class Class;
class ClassLoader;
class DexCache;
class IfTable;
class Object;
template <typename MirrorType> class ObjectArray;
class PointerArray;
class String;



#define PACKED(x) __attribute__ ((__aligned__(x), __packed__))
#define MANAGED PACKED(4)

template<bool kPoisonReferences, class MirrorType>
class MANAGED ObjectReference{};

template<class MirrorType>
class MANAGED CompressedReference : public ObjectReference<false, MirrorType>{};

}
///////////////////////////////////////////////////////////

class Thread;


template<class MirrorType>
class ObjPtr;

enum class PointerSize : size_t {
k32 = 4,
k64 = 8
};

static constexpr PointerSize kRuntimePointerSize = sizeof(void*) == 8U
                                                   ? PointerSize::k64
                                                   : PointerSize::k32;

enum ReadBarrierOption {
      kWithReadBarrier,     // Perform a read barrier.
      kWithoutReadBarrier,  // Don't perform a read barrier.
    };

class Offset {
public:
    explicit Offset(size_t val) : val_(val) {}
    int32_t Int32Value() const {
        return static_cast<int32_t>(val_);
    }
    uint32_t Uint32Value() const {
        return static_cast<uint32_t>(val_);
    }
    size_t SizeValue() const {
        return val_;
    }

protected:
    size_t val_;
};

class MemberOffset : public Offset {
public:
    explicit MemberOffset(size_t val) : Offset(val) {}
};


template<typename T>
constexpr int CTZ(T x) {
      static_assert(std::is_integral<T>::value, "T must be integral");
      // It is not unreasonable to ask for trailing zeros in a negative number. As such, do not check
      // that T is an unsigned type.
      static_assert(sizeof(T) == sizeof(uint64_t) || sizeof(T) <= sizeof(uint32_t),
                                      "Unsupported sizeof(T)");
      return (sizeof(T) == sizeof(uint64_t)) ? __builtin_ctzll(x) : __builtin_ctz(x);
}

class HiddenApiAccessFlags{
      public:
    enum ApiList {
            kWhitelist = 0,
            kLightGreylist,
            kDarkGreylist,
            kBlacklist,
          };
      };

template <typename T>
class ArrayRef;
class Primitive{
public:
      enum Type {
            kPrimNot = 0,
            kPrimBoolean,
            kPrimByte,
            kPrimChar,
            kPrimShort,
            kPrimInt,
            kPrimLong,
            kPrimFloat,
            kPrimDouble,
            kPrimVoid,
            kPrimLast = kPrimVoid
          };
};
class CodeItemInstructionAccessor{};

class CodeItemDataAccessor : public CodeItemInstructionAccessor{};
class CodeItemDebugInfoAccessor : public CodeItemDataAccessor{};

template<class MirrorType>
class GcRoot{

private:
    mutable mirror::CompressedReference<mirror::Object> root_;
public:
    ALWAYS_INLINE mirror::CompressedReference<mirror::Object>* AddressWithoutBarrier() {
            return &root_;
          }
};

    template <typename T>
    struct Identity {
      using type = T;
    };

    template<typename T>
    constexpr T RoundDown(T x, typename Identity<T>::type n) ;

    template<typename T>
    constexpr T RoundDown(T x, typename Identity<T>::type n) {
          return (x & -n);
        }

template<typename T>
constexpr T RoundUp(T x, typename std::remove_reference<T>::type n) ;

template<typename T>
constexpr T RoundUp(T x, typename std::remove_reference<T>::type n) {
      return RoundDown(x + n - 1, n);
    }


    template <typename Dest, typename Source>
    constexpr Dest dchecked_integral_cast(Source source) {

          return static_cast<Dest>(source);
        }
//////////////////////////////////////////////////

class ArtMethod FINAL {
 public:
  // Should the class state be checked on sensitive operations?
//  DECLARE_RUNTIME_DEBUG_FLAG(kCheckDeclaringClassState);

  // The runtime dex_method_index is kDexNoIndex. To lower dependencies, we use this
  // constexpr, and ensure that the value is correct in art_method.cc.
  static constexpr uint32_t kRuntimeMethodDexMethodIndex = 0xFFFFFFFF;

  ArtMethod() : access_flags_(0), dex_code_item_offset_(0), dex_method_index_(0),
      method_index_(0), hotness_count_(0) { }

  ArtMethod(ArtMethod* src, PointerSize image_pointer_size) {
    CopyFrom(src, image_pointer_size);
  }

  static ArtMethod* FromReflectedMethod(const ScopedObjectAccessAlreadyRunnable& soa,
                                        jobject jlr_method);

  template <ReadBarrierOption kReadBarrierOption = kWithReadBarrier>
  ALWAYS_INLINE mirror::Class* GetDeclaringClass() ;

  template <ReadBarrierOption kReadBarrierOption = kWithReadBarrier>
  ALWAYS_INLINE mirror::Class* GetDeclaringClassUnchecked();

  mirror::CompressedReference<mirror::Object>* GetDeclaringClassAddressWithoutBarrier() {
    return declaring_class_.AddressWithoutBarrier();
  }

  void SetDeclaringClass(ObjPtr<mirror::Class> new_declaring_class);

  bool CASDeclaringClass(mirror::Class* expected_class, mirror::Class* desired_class);

  static MemberOffset DeclaringClassOffset() {
    return MemberOffset(OFFSETOF_MEMBER(ArtMethod, declaring_class_));
  }

  // Note: GetAccessFlags acquires the mutator lock in debug mode to check that it is not called for
  // a proxy method.
  template <ReadBarrierOption kReadBarrierOption = kWithReadBarrier>
  uint32_t GetAccessFlags() {
    return access_flags_.load(std::memory_order_relaxed);
  }

  // This version should only be called when it's certain there is no
  // concurrency so there is no need to guarantee atomicity. For example,
  // before the method is linked.
  void SetAccessFlags(uint32_t new_access_flags) {
    access_flags_.store(new_access_flags, std::memory_order_relaxed);
  }

  static MemberOffset AccessFlagsOffset() {
    return MemberOffset(OFFSETOF_MEMBER(ArtMethod, access_flags_));
  }

  // Approximate what kind of method call would be used for this method.
  InvokeType GetInvokeType();

  // Returns true if the method is declared public.
  bool IsPublic() {
    return (GetAccessFlags() & kAccPublic) != 0;
  }

  // Returns true if the method is declared private.
  bool IsPrivate() {
    return (GetAccessFlags() & kAccPrivate) != 0;
  }

  // Returns true if the method is declared static.
  bool IsStatic() {
    return (GetAccessFlags() & kAccStatic) != 0;
  }

  // Returns true if the method is a constructor according to access flags.
  bool IsConstructor() {
    return (GetAccessFlags() & kAccConstructor) != 0;
  }

  // Returns true if the method is a class initializer according to access flags.
  bool IsClassInitializer() {
    return IsConstructor() && IsStatic();
  }

  // Returns true if the method is static, private, or a constructor.
  bool IsDirect() {
    return IsDirect(GetAccessFlags());
  }

  static bool IsDirect(uint32_t access_flags) {
    constexpr uint32_t direct = kAccStatic | kAccPrivate | kAccConstructor;
    return (access_flags & direct) != 0;
  }

  // Returns true if the method is declared synchronized.
  bool IsSynchronized() {
    constexpr uint32_t synchonized = kAccSynchronized | kAccDeclaredSynchronized;
    return (GetAccessFlags() & synchonized) != 0;
  }

  template <ReadBarrierOption kReadBarrierOption = kWithReadBarrier>
  bool IsFinal() {
    return (GetAccessFlags<kReadBarrierOption>() & kAccFinal) != 0;
  }

  template <ReadBarrierOption kReadBarrierOption = kWithReadBarrier>
  bool IsIntrinsic() {
    return (GetAccessFlags<kReadBarrierOption>() & kAccIntrinsic) != 0;
  }

  ALWAYS_INLINE void SetIntrinsic(uint32_t intrinsic) ;

  uint32_t GetIntrinsic() {
    static const int kAccFlagsShift = CTZ(kAccIntrinsicBits);
    return (GetAccessFlags() & kAccIntrinsicBits) >> kAccFlagsShift;
  }

  void SetNotIntrinsic() ;

  bool IsCopied() {
    static_assert((kAccCopied & (kAccIntrinsic | kAccIntrinsicBits)) == 0,
                  "kAccCopied conflicts with intrinsic modifier");
    const bool copied = (GetAccessFlags() & kAccCopied) != 0;
    // (IsMiranda() || IsDefaultConflicting()) implies copied
    return copied;
  }

  bool IsMiranda() {
    // The kAccMiranda flag value is used with a different meaning for native methods,
    // so we need to check the kAccNative flag as well.
    return (GetAccessFlags() & (kAccNative | kAccMiranda)) == kAccMiranda;
  }

  // Returns true if invoking this method will not throw an AbstractMethodError or
  // IncompatibleClassChangeError.
  bool IsInvokable() {
    return !IsAbstract() && !IsDefaultConflicting();
  }

  bool IsCompilable() {
    if (IsIntrinsic()) {
      // kAccCompileDontBother overlaps with kAccIntrinsicBits.
      return true;
    }
    return (GetAccessFlags() & kAccCompileDontBother) == 0;
  }

  void SetDontCompile() {
    AddAccessFlags(kAccCompileDontBother);
  }

  // A default conflict method is a special sentinel method that stands for a conflict between
  // multiple default methods. It cannot be invoked, throwing an IncompatibleClassChangeError if one
  // attempts to do so.
  bool IsDefaultConflicting() {
    if (IsIntrinsic()) {
      return false;
    }
    return (GetAccessFlags() & kAccDefaultConflict) != 0u;
  }

  // This is set by the class linker.
  template <ReadBarrierOption kReadBarrierOption = kWithReadBarrier>
  bool IsDefault() {
    static_assert((kAccDefault & (kAccIntrinsic | kAccIntrinsicBits)) == 0,
                  "kAccDefault conflicts with intrinsic modifier");
    return (GetAccessFlags<kReadBarrierOption>() & kAccDefault) != 0;
  }

  template <ReadBarrierOption kReadBarrierOption = kWithReadBarrier>
  bool IsObsolete() {
    return (GetAccessFlags<kReadBarrierOption>() & kAccObsoleteMethod) != 0;
  }

  void SetIsObsolete() {
    AddAccessFlags(kAccObsoleteMethod);
  }

  template <ReadBarrierOption kReadBarrierOption = kWithReadBarrier>
  bool IsNative() {
    return (GetAccessFlags<kReadBarrierOption>() & kAccNative) != 0;
  }

  // Checks to see if the method was annotated with @dalvik.annotation.optimization.FastNative.
  bool IsFastNative() {
    // The presence of the annotation is checked by ClassLinker and recorded in access flags.
    // The kAccFastNative flag value is used with a different meaning for non-native methods,
    // so we need to check the kAccNative flag as well.
    constexpr uint32_t mask = kAccFastNative | kAccNative;
    return (GetAccessFlags() & mask) == mask;
  }

  // Checks to see if the method was annotated with @dalvik.annotation.optimization.CriticalNative.
  bool IsCriticalNative() {
    // The presence of the annotation is checked by ClassLinker and recorded in access flags.
    // The kAccCriticalNative flag value is used with a different meaning for non-native methods,
    // so we need to check the kAccNative flag as well.
    constexpr uint32_t mask = kAccCriticalNative | kAccNative;
    return (GetAccessFlags() & mask) == mask;
  }

  template <ReadBarrierOption kReadBarrierOption = kWithReadBarrier>
  bool IsAbstract() {
    return (GetAccessFlags<kReadBarrierOption>() & kAccAbstract) != 0;
  }

  bool IsSynthetic() {
    return (GetAccessFlags() & kAccSynthetic) != 0;
  }

  bool IsVarargs() {
    return (GetAccessFlags() & kAccVarargs) != 0;
  }

  bool IsProxyMethod() ;

  bool IsPolymorphicSignature() ;

  bool SkipAccessChecks() {
    // The kAccSkipAccessChecks flag value is used with a different meaning for native methods,
    // so we need to check the kAccNative flag as well.
    return (GetAccessFlags() & (kAccSkipAccessChecks | kAccNative)) == kAccSkipAccessChecks;
  }

  void SetSkipAccessChecks() {
    // SkipAccessChecks() is applicable only to non-native methods.
    AddAccessFlags(kAccSkipAccessChecks);
  }

  bool PreviouslyWarm() {
    if (IsIntrinsic()) {
      // kAccPreviouslyWarm overlaps with kAccIntrinsicBits.
      return true;
    }
    return (GetAccessFlags() & kAccPreviouslyWarm) != 0;
  }

  template <ReadBarrierOption kReadBarrierOption = kWithReadBarrier>
  void SetPreviouslyWarm() {
    if (IsIntrinsic<kReadBarrierOption>()) {
      // kAccPreviouslyWarm overlaps with kAccIntrinsicBits.
      return;
    }
    AddAccessFlags<kReadBarrierOption>(kAccPreviouslyWarm);
  }

  // Should this method be run in the interpreter and count locks (e.g., failed structured-
  // locking verification)?
  bool MustCountLocks() {
    if (IsIntrinsic()) {
      return false;
    }
    return (GetAccessFlags() & kAccMustCountLocks) != 0;
  }

  void SetMustCountLocks() {
    AddAccessFlags(kAccMustCountLocks);
  }

  HiddenApiAccessFlags::ApiList GetHiddenApiAccessFlags() ;

  // Returns true if this method could be overridden by a default method.
  bool IsOverridableByDefaultMethod() ;

  bool CheckIncompatibleClassChange(InvokeType type) ;

  // Throws the error that would result from trying to invoke this method (i.e.
  // IncompatibleClassChangeError or AbstractMethodError). Only call if !IsInvokable();
  void ThrowInvocationTimeError() ;

  uint16_t GetMethodIndex() ;

  // Doesn't do erroneous / unresolved class checks.
  uint16_t GetMethodIndexDuringLinking() ;

  size_t GetVtableIndex()  {
    return GetMethodIndex();
  }

  void SetMethodIndex(uint16_t new_method_index)  {
    // Not called within a transaction.
    method_index_ = new_method_index;
  }

  static MemberOffset DexMethodIndexOffset() {
    return MemberOffset(OFFSETOF_MEMBER(ArtMethod, dex_method_index_));
  }

  static MemberOffset MethodIndexOffset() {
    return MemberOffset(OFFSETOF_MEMBER(ArtMethod, method_index_));
  }

  uint32_t GetCodeItemOffset() {
    return dex_code_item_offset_;
  }

  void SetCodeItemOffset(uint32_t new_code_off) {
    // Not called within a transaction.
    dex_code_item_offset_ = new_code_off;
  }

  // Number of 32bit registers that would be required to hold all the arguments
  static size_t NumArgRegisters(const StringPiece& shorty);

  ALWAYS_INLINE uint32_t GetDexMethodIndexUnchecked() {
    return dex_method_index_;
  }
  template <ReadBarrierOption kReadBarrierOption = kWithReadBarrier>
  ALWAYS_INLINE uint32_t GetDexMethodIndex() ;

  void SetDexMethodIndex(uint32_t new_idx) {
    // Not called within a transaction.
    dex_method_index_ = new_idx;
  }

  // Lookup the Class* from the type index into this method's dex cache.
  ObjPtr<mirror::Class> LookupResolvedClassFromTypeIndex(dex::TypeIndex type_idx)
      ;
  // Resolve the Class* from the type index into this method's dex cache.
  ObjPtr<mirror::Class> ResolveClassFromTypeIndex(dex::TypeIndex type_idx)
      ;

  // Returns true if this method has the same name and signature of the other method.
  bool HasSameNameAndSignature(ArtMethod* other) ;

  // Find the method that this method overrides.
  ArtMethod* FindOverriddenMethod(PointerSize pointer_size)
      ;

  // Find the method index for this method within other_dexfile. If this method isn't present then
  // return dex::kDexNoIndex. The name_and_signature_idx MUST refer to a MethodId with the same
  // name and signature in the other_dexfile, such as the method index used to resolve this method
  // in the other_dexfile.
  uint32_t FindDexMethodIndexInOtherDexFile(const DexFile& other_dexfile,
                                            uint32_t name_and_signature_idx)
      ;

  void Invoke(Thread* self, uint32_t* args, uint32_t args_size, JValue* result, const char* shorty)
      ;

  const void* GetEntryPointFromQuickCompiledCode() {
    return GetEntryPointFromQuickCompiledCodePtrSize(kRuntimePointerSize);
  }
  ALWAYS_INLINE const void* GetEntryPointFromQuickCompiledCodePtrSize(PointerSize pointer_size) {
    return GetNativePointer<const void*>(
        EntryPointFromQuickCompiledCodeOffset(pointer_size), pointer_size);
  }

  void SetEntryPointFromQuickCompiledCode(const void* entry_point_from_quick_compiled_code) {
    SetEntryPointFromQuickCompiledCodePtrSize(entry_point_from_quick_compiled_code,
                                              kRuntimePointerSize);
  }
  ALWAYS_INLINE void SetEntryPointFromQuickCompiledCodePtrSize(
      const void* entry_point_from_quick_compiled_code, PointerSize pointer_size) {
    SetNativePointer(EntryPointFromQuickCompiledCodeOffset(pointer_size),
                     entry_point_from_quick_compiled_code,
                     pointer_size);
  }

  // Registers the native method and returns the new entry point. NB The returned entry point might
  // be different from the native_method argument if some MethodCallback modifies it.
  const void* RegisterNative(const void* native_method);

  void UnregisterNative() ;

  static MemberOffset DataOffset(PointerSize pointer_size) {
    return MemberOffset(PtrSizedFieldsOffset(pointer_size) + OFFSETOF_MEMBER(
        PtrSizedFields, data_) / sizeof(void*) * static_cast<size_t>(pointer_size));
  }

  static MemberOffset EntryPointFromJniOffset(PointerSize pointer_size) {
    return DataOffset(pointer_size);
  }

  static MemberOffset EntryPointFromQuickCompiledCodeOffset(PointerSize pointer_size) {
    return MemberOffset(PtrSizedFieldsOffset(pointer_size) + OFFSETOF_MEMBER(
        PtrSizedFields, entry_point_from_quick_compiled_code_) / sizeof(void*)
            * static_cast<size_t>(pointer_size));
  }

  ImtConflictTable* GetImtConflictTable(PointerSize pointer_size) {
    return reinterpret_cast<ImtConflictTable*>(GetDataPtrSize(pointer_size));
  }

  ALWAYS_INLINE void SetImtConflictTable(ImtConflictTable* table, PointerSize pointer_size) {
    SetDataPtrSize(table, pointer_size);
  }

  ProfilingInfo* GetProfilingInfo(PointerSize pointer_size)  {
    // Don't do a read barrier in the DCHECK() inside GetAccessFlags() called by IsNative(),
    // as GetProfilingInfo is called in places where the declaring class is treated as a weak
    // reference (accessing it with a read barrier would either prevent unloading the class,
    // or crash the runtime if the GC wants to unload it).
    return reinterpret_cast<ProfilingInfo*>(GetDataPtrSize(pointer_size));
  }

  ALWAYS_INLINE void SetProfilingInfo(ProfilingInfo* info) {
    SetDataPtrSize(info, kRuntimePointerSize);
  }

  ALWAYS_INLINE void SetProfilingInfoPtrSize(ProfilingInfo* info, PointerSize pointer_size) {
    SetDataPtrSize(info, pointer_size);
  }

  static MemberOffset ProfilingInfoOffset() {
    return DataOffset(kRuntimePointerSize);
  }

  template <ReadBarrierOption kReadBarrierOption = kWithReadBarrier>
  ALWAYS_INLINE bool HasSingleImplementation() ;

  ALWAYS_INLINE void SetHasSingleImplementation(bool single_impl) {
    if (single_impl) {
      AddAccessFlags(kAccSingleImplementation);
    } else {
      ClearAccessFlags(kAccSingleImplementation);
    }
  }

  // Takes a method and returns a 'canonical' one if the method is default (and therefore
  // potentially copied from some other class). For example, this ensures that the debugger does not
  // get confused as to which method we are in.
  ArtMethod* GetCanonicalMethod(PointerSize pointer_size = kRuntimePointerSize)
      ;

  template <ReadBarrierOption kReadBarrierOption = kWithReadBarrier>
  ArtMethod* GetSingleImplementation(PointerSize pointer_size)
      ;

  template <ReadBarrierOption kReadBarrierOption = kWithReadBarrier>
  ALWAYS_INLINE void SetSingleImplementation(ArtMethod* method, PointerSize pointer_size) {
    SetDataPtrSize(method, pointer_size);
  }

  void* GetEntryPointFromJni() {
    return GetEntryPointFromJniPtrSize(kRuntimePointerSize);
  }

  ALWAYS_INLINE void* GetEntryPointFromJniPtrSize(PointerSize pointer_size) {
    return GetDataPtrSize(pointer_size);
  }

  void SetEntryPointFromJni(const void* entrypoint) {
    SetEntryPointFromJniPtrSize(entrypoint, kRuntimePointerSize);
  }

  ALWAYS_INLINE void SetEntryPointFromJniPtrSize(const void* entrypoint, PointerSize pointer_size) {
    SetDataPtrSize(entrypoint, pointer_size);
  }

  ALWAYS_INLINE void* GetDataPtrSize(PointerSize pointer_size) {
    return GetNativePointer<void*>(DataOffset(pointer_size), pointer_size);
  }

  ALWAYS_INLINE void SetDataPtrSize(const void* data, PointerSize pointer_size) {
    SetNativePointer(DataOffset(pointer_size), data, pointer_size);
  }

  // Is this a CalleSaveMethod or ResolutionMethod and therefore doesn't adhere to normal
  // conventions for a method of managed code. Returns false for Proxy methods.
  ALWAYS_INLINE bool IsRuntimeMethod() {
    return dex_method_index_ == kRuntimeMethodDexMethodIndex;
  }

  // Is this a hand crafted method used for something like describing callee saves?
  bool IsCalleeSaveMethod() ;

  bool IsResolutionMethod() ;

  bool IsImtUnimplementedMethod() ;

  // Find the catch block for the given exception type and dex_pc. When a catch block is found,
  // indicates whether the found catch block is responsible for clearing the exception or whether
  // a move-exception instruction is present.
  uint32_t FindCatchBlock(Handle<mirror::Class> exception_type, uint32_t dex_pc,
                          bool* has_no_move_exception)
      ;

  // NO_THREAD_SAFETY_ANALYSIS since we don't know what the callback requires.
  template<ReadBarrierOption kReadBarrierOption = kWithReadBarrier, typename RootVisitorType>
  void VisitRoots(RootVisitorType& visitor, PointerSize pointer_size) ;

  const DexFile* GetDexFile() ;

  const char* GetDeclaringClassDescriptor() ;

  ALWAYS_INLINE const char* GetShorty() ;

  const char* GetShorty(uint32_t* out_length) ;

  const Signature GetSignature() ;

  ALWAYS_INLINE const char* GetName() ;

  ObjPtr<mirror::String> GetNameAsString(Thread* self) ;

  const DexFile::CodeItem* GetCodeItem() ;

  bool IsResolvedTypeIdx(dex::TypeIndex type_idx) ;

  int32_t GetLineNumFromDexPC(uint32_t dex_pc) ;

  const DexFile::ProtoId& GetPrototype() ;

  const DexFile::TypeList* GetParameterTypeList() ;

  const char* GetDeclaringClassSourceFile() ;

  uint16_t GetClassDefIndex() ;

  const DexFile::ClassDef& GetClassDef() ;

  ALWAYS_INLINE size_t GetNumberOfParameters() ;

  const char* GetReturnTypeDescriptor() ;

  ALWAYS_INLINE Primitive::Type GetReturnTypePrimitive() ;

  const char* GetTypeDescriptorFromTypeIdx(dex::TypeIndex type_idx)
      ;

  // Lookup return type.
  ObjPtr<mirror::Class> LookupResolvedReturnType() ;
  // Resolve return type. May cause thread suspension due to GetClassFromTypeIdx
  // calling ResolveType this caused a large number of bugs at call sites.
  ObjPtr<mirror::Class> ResolveReturnType() ;

  mirror::ClassLoader* GetClassLoader() ;

  template <ReadBarrierOption kReadBarrierOption = kWithReadBarrier>
  mirror::DexCache* GetDexCache() ;
  mirror::DexCache* GetObsoleteDexCache() ;

  ALWAYS_INLINE ArtMethod* GetInterfaceMethodForProxyUnchecked(PointerSize pointer_size)
      ;
  ALWAYS_INLINE ArtMethod* GetInterfaceMethodIfProxy(PointerSize pointer_size)
      ;

  ArtMethod* GetNonObsoleteMethod() ;

  // May cause thread suspension due to class resolution.
  bool EqualParameters(Handle<mirror::ObjectArray<mirror::Class>> params)
      ;

  // Size of an instance of this native class.
  static size_t Size(PointerSize pointer_size) {
    return PtrSizedFieldsOffset(pointer_size) +
        (sizeof(PtrSizedFields) / sizeof(void*)) * static_cast<size_t>(pointer_size);
  }

  // Alignment of an instance of this native class.
  static size_t Alignment(PointerSize pointer_size) {
    // The ArtMethod alignment is the same as image pointer size. This differs from
    // alignof(ArtMethod) if cross-compiling with pointer_size != sizeof(void*).
    return static_cast<size_t>(pointer_size);
  }

  void CopyFrom(ArtMethod* src, PointerSize image_pointer_size)
      ;

  // Note, hotness_counter_ updates are non-atomic but it doesn't need to be precise.  Also,
  // given that the counter is only 16 bits wide we can expect wrap-around in some
  // situations.  Consumers of hotness_count_ must be able to deal with that.
  uint16_t IncrementCounter() {
    return ++hotness_count_;
  }

  void ClearCounter() {
    hotness_count_ = 0;
  }

  void SetCounter(int16_t hotness_count) {
    hotness_count_ = hotness_count;
  }

  uint16_t GetCounter() const {
    return hotness_count_;
  }

  static MemberOffset HotnessCountOffset() {
    return MemberOffset(OFFSETOF_MEMBER(ArtMethod, hotness_count_));
  }

  ArrayRef<const uint8_t> GetQuickenedInfo() ;
  uint16_t GetIndexFromQuickening(uint32_t dex_pc) ;

  // Returns the method header for the compiled code containing 'pc'. Note that runtime
  // methods will return null for this method, as they are not oat based.
  const OatQuickMethodHeader* GetOatQuickMethodHeader(uintptr_t pc)
      ;

  // Get compiled code for the method, return null if no code exists.
  const void* GetOatMethodQuickCode(PointerSize pointer_size)
      ;

  // Returns whether the method has any compiled code, JIT or AOT.
  bool HasAnyCompiledCode() ;

  // Returns a human-readable signature for 'm'. Something like "a.b.C.m" or
  // "a.b.C.m(II)V" (depending on the value of 'with_signature').
  static std::string PrettyMethod(ArtMethod* m, bool with_signature = true)
      ;
  std::string PrettyMethod(bool with_signature = true)
      ;
  // Returns the JNI native function name for the non-overloaded method 'm'.
  std::string JniShortName()
      ;
  // Returns the JNI native function name for the overloaded method 'm'.
  std::string JniLongName()
      ;

  // Update heap objects and non-entrypoint pointers by the passed in visitor for image relocation.
  // Does not use read barrier.
  template <typename Visitor>
  ALWAYS_INLINE void UpdateObjectsForImageRelocation(const Visitor& visitor)
      ;

  // Update entry points by passing them through the visitor.
  template <ReadBarrierOption kReadBarrierOption = kWithReadBarrier, typename Visitor>
  ALWAYS_INLINE void UpdateEntrypoints(const Visitor& visitor, PointerSize pointer_size);

  // Visit the individual members of an ArtMethod.  Used by imgdiag.
  // As imgdiag does not support mixing instruction sets or pointer sizes (e.g., using imgdiag32
  // to inspect 64-bit images, etc.), we can go beneath the accessors directly to the class members.
  template <typename VisitorFunc>
  void VisitMembers(VisitorFunc& visitor) {
    visitor(this, &declaring_class_, "declaring_class_");
    visitor(this, &access_flags_, "access_flags_");
    visitor(this, &dex_code_item_offset_, "dex_code_item_offset_");
    visitor(this, &dex_method_index_, "dex_method_index_");
    visitor(this, &method_index_, "method_index_");
    visitor(this, &hotness_count_, "hotness_count_");
    visitor(this, &ptr_sized_fields_.data_, "ptr_sized_fields_.data_");
    visitor(this,
            &ptr_sized_fields_.entry_point_from_quick_compiled_code_,
            "ptr_sized_fields_.entry_point_from_quick_compiled_code_");
  }

  // Returns the dex instructions of the code item for the art method. Returns an empty array for
  // the null code item case.
  ALWAYS_INLINE CodeItemInstructionAccessor DexInstructions()
      ;

  // Returns the dex code item data section of the DexFile for the art method.
  ALWAYS_INLINE CodeItemDataAccessor DexInstructionData()
      ;

  // Returns the dex code item debug info section of the DexFile for the art method.
  ALWAYS_INLINE CodeItemDebugInfoAccessor DexInstructionDebugInfo()
      ;

 protected:
  // Field order required by test "ValidateFieldOrderOfJavaCppUnionClasses".
  // The class we are a part of.
  GcRoot<mirror::Class> declaring_class_;

  // Access flags; low 16 bits are defined by spec.
  // Getting and setting this flag needs to be atomic when concurrency is
  // possible, e.g. after this method's class is linked. Such as when setting
  // verifier flags and single-implementation flag.
  std::atomic<std::uint32_t> access_flags_;

  /* Dex file fields. The defining dex file is available via declaring_class_->dex_cache_ */

  // Offset to the CodeItem.
  uint32_t dex_code_item_offset_;

  // Index into method_ids of the dex file associated with this method.
  uint32_t dex_method_index_;

  /* End of dex file fields. */

  // Entry within a dispatch table for this method. For static/direct methods the index is into
  // the declaringClass.directMethods, for virtual methods the vtable and for interface methods the
  // ifTable.
  uint16_t method_index_;

  // The hotness we measure for this method. Not atomic, as we allow
  // missing increments: if the method is hot, we will see it eventually.
  uint16_t hotness_count_;

  // Fake padding field gets inserted here.

  // Must be the last fields in the method.
  struct PtrSizedFields {
    // Depending on the method type, the data is
    //   - native method: pointer to the JNI function registered to this method
    //                    or a function to resolve the JNI function,
    //   - conflict method: ImtConflictTable,
    //   - abstract/interface method: the single-implementation if any,
    //   - proxy method: the original interface method or constructor,
    //   - other methods: the profiling data.
    void* data_;

    // Method dispatch from quick compiled code invokes this pointer which may cause bridging into
    // the interpreter.
    void* entry_point_from_quick_compiled_code_;
  } ptr_sized_fields_;

 private:
  uint16_t FindObsoleteDexClassDefIndex() ;

  static constexpr size_t PtrSizedFieldsOffset(PointerSize pointer_size) {
    // Round up to pointer size for padding field. Tested in art_method.cc.
    return RoundUp(offsetof(ArtMethod, hotness_count_) + sizeof(hotness_count_),
                   static_cast<size_t>(pointer_size));
  }

  // Compare given pointer size to the image pointer size.
  static bool IsImagePointerSize(PointerSize pointer_size);

  dex::TypeIndex GetReturnTypeIndex() ;

  template<typename T>
  ALWAYS_INLINE T GetNativePointer(MemberOffset offset, PointerSize pointer_size) const {
    static_assert(std::is_pointer<T>::value, "T must be a pointer type");
    const auto addr = reinterpret_cast<uintptr_t>(this) + offset.Uint32Value();
    if (pointer_size == PointerSize::k32) {
      return reinterpret_cast<T>(*reinterpret_cast<const uint32_t*>(addr));
    } else {
      auto v = *reinterpret_cast<const uint64_t*>(addr);
      return reinterpret_cast<T>(dchecked_integral_cast<uintptr_t>(v));
    }
  }

  template<typename T>
  ALWAYS_INLINE void SetNativePointer(MemberOffset offset, T new_value, PointerSize pointer_size) {
    static_assert(std::is_pointer<T>::value, "T must be a pointer type");
    const auto addr = reinterpret_cast<uintptr_t>(this) + offset.Uint32Value();
    if (pointer_size == PointerSize::k32) {
      uintptr_t ptr = reinterpret_cast<uintptr_t>(new_value);
      *reinterpret_cast<uint32_t*>(addr) = dchecked_integral_cast<uint32_t>(ptr);
    } else {
      *reinterpret_cast<uint64_t*>(addr) = reinterpret_cast<uintptr_t>(new_value);
    }
  }

  template <ReadBarrierOption kReadBarrierOption> void GetAccessFlagsDCheck();

  static inline bool IsValidIntrinsicUpdate(uint32_t modifier) {
    return (((modifier & kAccIntrinsic) == kAccIntrinsic) &&
            (((modifier & ~(kAccIntrinsic | kAccIntrinsicBits)) == 0)));
  }

  static inline bool OverlapsIntrinsicBits(uint32_t modifier) {
    return (modifier & kAccIntrinsicBits) != 0;
  }

  // This setter guarantees atomicity.
  template <ReadBarrierOption kReadBarrierOption = kWithReadBarrier>
  void AddAccessFlags(uint32_t flag) {
    uint32_t old_access_flags;
    uint32_t new_access_flags;
    do {
      old_access_flags = access_flags_.load(std::memory_order_relaxed);
      new_access_flags = old_access_flags | flag;
    } while (!access_flags_.compare_exchange_weak(old_access_flags, new_access_flags));
  }

  // This setter guarantees atomicity.
  void ClearAccessFlags(uint32_t flag) {
    uint32_t old_access_flags;
    uint32_t new_access_flags;
    do {
      old_access_flags = access_flags_.load(std::memory_order_relaxed);
      new_access_flags = old_access_flags & ~flag;
    } while (!access_flags_.compare_exchange_weak(old_access_flags, new_access_flags));
  }

  DISALLOW_COPY_AND_ASSIGN(ArtMethod);  // Need to use CopyFrom to deal with 32 vs 64 bits.
};
// namespace mirror

class MethodCallback {
 public:
  virtual ~MethodCallback() {}

  virtual void RegisterNativeMethod(ArtMethod* method,
                                    const void* original_implementation,
                                    /*out*/void** new_implementation)
       = 0;
};

namespace mirror{
    template <typename T> struct NativeDexCachePair;
    using MethodDexCachePair = NativeDexCachePair<ArtMethod>;
    using MethodDexCacheType = std::atomic<MethodDexCachePair>;
}

}  // namespace art

#endif  // ART_RUNTIME_ART_METHOD_H_
