package com.ujamaaonline.customer.utils;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.Primitives;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import static android.content.Context.LOCATION_SERVICE;

public class ObjectUtil {

    public static final String GetterPrefix = "get";
    public static final String KeyPathSeparatorRegEx = "\\.";

    public static boolean isEmpty(final Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof String) {
            return isEmptyStr((String) obj);
        }
        if (obj instanceof CharSequence) {
            return ((CharSequence) obj).length() == 0;
        }
        if (obj instanceof Map) {
            return ((Map<?, ?>) obj).size() == 0;
        }
        if (obj instanceof Collection) {
            return ((Collection<?>) obj).isEmpty();
        }
        if (obj.getClass().isArray()) {
            return ((Object[]) obj).length == 0;
        }
        return false;
    }

    public static void showPermissionDialog(Context context) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("Please turn on Location Permission(permissions/Location)");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                        intent.setData(uri);
                        context.startActivity(intent);
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }

    public static Location getLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return null;
        }
        Location myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (myLocation == null) {
            myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }
        return myLocation;
    }




    public static boolean isEmptyStr(final String str) {
        return str == null || str.trim().length() == 0 || str.equalsIgnoreCase("null");
    }

    public static boolean isNonEmptyStr(String str) {
        return !isEmptyStr(str);
    }

    public static boolean isEmptyView(View view) {
        return isEmptyStr(getTextFromView(view));
    }

    public static String getTextFromView(View view) {
        return (view != null && view instanceof TextView) ? ((TextView) view).getText().toString().trim() : "";
    }

    public static <T> T objectForClass(String className, Class<T> entity) {
        return Primitives.wrap(entity).cast(objectForClass(className));
    }

    public static <T> T objectForClass(String className) {
        Object object = null;
        try {
            Class<?> classObj = Class.forName(className);
            object = objectForClass(classObj);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return (T) object;
    }

    public static <T> T objectForClass(Class<T> classObj) {
        try {
            if (classObj.getModifiers() == 0) {
                try {
                    Constructor<?>[] ctors = classObj.getDeclaredConstructors();
                    Constructor<?> ctor;
                    for (Constructor<?> ctor1 : ctors) {
                        ctor = ctor1;
                        if (ctor.getGenericParameterTypes().length == 0) {
                            break;
                        }
                    }
                    ctor = classObj.getDeclaredConstructor();
                    try {
                        ctor.setAccessible(true);
                        return (T) ctor.newInstance();
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                return classObj.newInstance();
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String methodNameForKeyWithPrefix(String aKey, String aPrefix) {
        if (aKey != null) {
            if (aPrefix != null) {
                StringBuilder aBuffer = new StringBuilder();
                char[] someChars = aKey.toCharArray();
                someChars[0] = Character.toUpperCase(someChars[0]);
                aBuffer.append(aPrefix);
                aBuffer.append(someChars);
                return aBuffer.toString();
            }
            throw new IllegalArgumentException("RuntimeKeyValue.methodNameForKeyWithPrefix: null prefix.");
        }
        throw new IllegalArgumentException("RuntimeKeyValue.methodNameForKeyWithPrefix: null key.");
    }

    public static Class<?> classForObject(Object anObject) {
        if (anObject != null) {
            Class<?> aClass = anObject.getClass();
            if (anObject instanceof Class) {
                aClass = (Class<?>) anObject;
            }
            return aClass;
        }
        throw new IllegalArgumentException("Runtime.classForObject: null object.");
    }

    public static Method getterMethodWithNameInClass(String aName, Class<?> aClass, Class<?>[] paramTypes) {
        if (aName != null) {
            if (aClass != null) {
                Method aMethod = null;

                try {
                    aMethod = aClass.getDeclaredMethod(aName, paramTypes);
                    aMethod.setAccessible(true);
                    if (aMethod.getReturnType().equals(Void.TYPE)) {
                        aMethod = null;
                    }
                } catch (NoSuchMethodException anException) {
                } catch (SecurityException anException) {
                }
                if (aMethod == null) {
                    aClass = aClass.getSuperclass();
                    if (aClass != null) {
                        aMethod = getterMethodWithNameInClass(aName, aClass, paramTypes);
                    }
                }
                return aMethod;
            }
            throw new IllegalArgumentException("Runtime.getterMethodWithNameInClass: null class.");
        }
        throw new IllegalArgumentException("Runtime.getterMethodWithNameInClass:null entityName.");
    }

    public static Field setterFieldWithNameInClass(String fieldName, Class<?> aClass, Class<?> fldClass) {
        if (fieldName == null || aClass == null) {
            return null;
        }
        try {
            Field wantedField = null;
            Field[] fields = aClass.getFields();
            if (fields != null && fields.length > 0) {
                for (Field currentField : fields) {
                    String sfieldName = currentField.getName();
                    if (fieldName.equalsIgnoreCase(sfieldName)) {
                        if (wantedField == null) {
                            currentField.setAccessible(true);
                            if (currentField.isAccessible()) {
                                wantedField = currentField;
                            }
                        }
                        if (fldClass == currentField.getClass()) {
                            wantedField = currentField;
                        }
                    }
                }
                return wantedField;
            }
        } catch (SecurityException anException) {
            anException.printStackTrace();
        }
        return null;
    }

    public static Field fieldWithSerializedName(String name, Class<?> aClass) {
        if (isEmptyStr(name) || aClass == null) {
            return null;
        }
        try {
            Field wantedField = null;
            Field[] fields = aClass.getFields();
            if (fields != null && fields.length > 0) {
                for (Field currentField : fields) {
                    if (!currentField.isAnnotationPresent(SerializedName.class)) {
                        continue;
                    }
                    SerializedName serializedName = currentField.getAnnotation(SerializedName.class);
                    if (name.equals(serializedName.value())) {
                        currentField.setAccessible(true);
                        if (currentField.isAccessible()) {
                            wantedField = currentField;
                        }
                        break;
                    }
                }
                return wantedField;
            }
        } catch (SecurityException anException) {
            anException.printStackTrace();
        }
        return null;
    }

    public static Object invokeMethod(String methodName, Object obj) {
        Object result = null;
        try {
            result = obj.getClass().getMethod(methodName).invoke(obj);
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public static boolean boolValue(final Object v) {
        if (v == null) {
            return false;
        }
        if (v instanceof String) {
            if (v.equals("")) {
                return false;
            }
            String s = (String) v;
            char c0 = s.charAt(0);
            if ((c0 == 'Y' || c0 == 'y') && s.equalsIgnoreCase("YES")) {
                return true;
            }
            if ((c0 == 'T' || c0 == 't') && s.equalsIgnoreCase("TRUE")) {
                return true;
            }
            return s.length() == 1 && c0 == '1';
        }
        if (v instanceof Boolean) {
            return (Boolean) v;
        }
        if (v instanceof Number) {
            return ((Number) v).intValue() != 0;
        }
        if (v instanceof Collection) {
            return ((Collection<?>) v).size() > 0;
        }
        return true;
    }

    public static long longValue(final Object v) {
        if (v == null) {
            return 0;
        }
        if (v instanceof Number) {
            return ((Number) v).longValue();
        }
        if (v instanceof String) {
            String s = (String) v;
            if (s.length() == 0) {
                return 0;
            }
            try {
                if (s.indexOf('.') >= 0) {
                    return new BigDecimal(s).longValue();
                }
                return Long.parseLong(s);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return longValue(v.toString());
    }

    public static float floatValue(final Object v) {
        if (v == null) {
            return 0;
        }
        if (v instanceof Number) {
            return ((Number) v).floatValue();
        }
        if (v instanceof String) {
            String s = (String) v;
            if (s.length() == 0) {
                return 0;
            }
            try {
                if (s.indexOf('.') >= 0) {
                    return new BigDecimal(s).floatValue();
                }
                return Float.parseFloat(s);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return floatValue(v.toString());
    }

    public static int intValue(final Object v) {
        if (v == null) {
            return 0;
        }
        if (v instanceof Number) {
            return ((Number) v).intValue();
        }
        if (v instanceof String) {
            String s = (String) v;
            if (s.trim().length() == 0) {
                return 0;
            }
            try {
                if (s.indexOf('.') >= 0) {
                    return new BigDecimal(s).intValue();
                }
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return intValue(v.toString());
    }

    public static boolean isNumber(final Object v) {
        if (isEmpty(v)) {
            return false;
        }
        CharSequence cs = (CharSequence) v;
        final int length = cs.length();
        for (int i = 0; i < length; i++) {
            if (!Character.isDigit(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static Object valueForSerializedName(String name, Object anObject) {
        if (isNonEmptyStr(name)) {
            if (anObject != null) {
                Class<?> aClass = classForObject(anObject);
                Field aField = fieldWithSerializedName(name, aClass);
                if (aField != null) {
                    Object aValue = null;
                    try {
                        aField.setAccessible(true);
                        aValue = aField.get(anObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return aValue;
                }
                throw new RuntimeException(
                        "RuntimeKeyValue.valueForKey: no getter method/Field found for SerializedName'" + name + "' in " + anObject.getClass() + ".");
            }
            throw new IllegalArgumentException("RuntimeKeyValue.valueForKey: null object.");
        }
        throw new IllegalArgumentException("RuntimeKeyValue.valueForKey: null key.");
    }

    public static Object valueForKey(String aKey, Object anObject) {
        if (aKey != null) {
            if (anObject != null) {
                Class<?> aClass = classForObject(anObject);
                String aName = methodNameForKeyWithPrefix(aKey, ObjectUtil.GetterPrefix);
                Method aMethod = getterMethodWithNameInClass(aName, aClass, new Class[]{});
                Field aField = setterFieldWithNameInClass(aKey, aClass, null);
                //First try and get the METHOD. If no method then go for the
                //Field. If the Field does not work either throw an exception
                if (aMethod == null) {
                    aMethod = getterMethodWithNameInClass(aKey, aClass, new Class[]{});
                }
                if (aMethod != null) {
                    Object aValue = null;
                    try {
                        aValue = aMethod.invoke(anObject);
                    } catch (Exception anException) {
                        // throw new RuntimeException("RuntimeKeyValue.valueForKey: aKey = " + aKey + " " + anObject.getClass() + " " + anException);
                    }
                    return aValue;
                } else if (aField != null) {
                    Object aValue = null;
                    try {
                        aField.setAccessible(true);
                        aValue = aField.get(anObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return aValue;
                }
                throw new RuntimeException("RuntimeKeyValue.valueForKey: no getter method/Field found for key'" + aKey + "' in " + anObject.getClass() + ".");
            }
            throw new IllegalArgumentException("RuntimeKeyValue.valueForKey: null object.");
        }
        throw new IllegalArgumentException("RuntimeKeyValue.valueForKey: null key.");
    }

    public static Map<String, Object> stringToMap(String mapString, String mapSeparator, String keyValueSeparator) {
        Map<String, Object> valueMap = new HashMap<>();
        if (isEmptyStr(mapString) || isEmptyStr(mapSeparator) || isEmptyStr(keyValueSeparator)) {
            return valueMap;
        }
        String[] mapValueArr = mapString.split(mapSeparator);
        if (mapValueArr.length == 0) {
            return valueMap;
        }
        for (String mapValue : mapValueArr) {
            String[] keyValueArr = mapValue.split(keyValueSeparator);
            if (keyValueArr.length < 2 || isEmptyStr(keyValueArr[0])) {
                continue;
            }
            valueMap.put(keyValueArr[0].trim(), keyValueArr[1].trim());
        }
        return valueMap;
    }

    public static String convertPasswordIntoMD5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();
            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


}
