package org.infinitytwogames.wispui.event.bus;

import org.infinitytwogames.wispui.event.Event;
import org.infinitytwogames.wispui.event.SubscribeEvent;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * A high-performance, reflection-based event distribution system.
 * <p>
 * The EventBus facilitates loose coupling by allowing objects to communicate
 * without direct references. It supports both instance-based and static
 * method subscriptions.
 * </p>
 * *
 * * <h2>Key Features</h2>
 * <ul>
 * <li><b>Hierarchy Support:</b> Automatically discovers @SubscribeEvent methods
 * in parent classes, supporting deep inheritance in UI components.</li>
 * <li><b>Polymorphic Dispatch:</b> Posting a sub-event (e.g., MouseClickedEvent)
 * will also trigger listeners for the parent event (e.g., MouseEvent or InputEvent).</li>
 * <li><b>Thread Safety (Simulation):</b> Uses local copies of listener lists during
 * dispatch to prevent ConcurrentModificationExceptions.</li>
 * </ul>
 */
public class EventBus {
    private static final EventBus global = new EventBus();
    private final Map<Class<?>, List<ListenerMethod>> subscribers = new HashMap<>();
    
    public static void dispatch(Event event) {
        global.post(event);
    }
    
    /**
     * Registers all non-static methods marked with @SubscribeEvent in the object.
     * <p>
     * Logic: Traverses the class hierarchy up to {@code Object.class} to ensure
     * that inherited event handlers are correctly registered.
     * </p>
     */
    public static void connect(Object obj) {
        global.register(obj);
    }
    
    public static void disconnect(Object obj) {
        global.unregister(obj);
    }
    
    /**
     * Registers all static methods marked with @SubscribeEvent in the object.
     * <p>
     * Logic: Traverses the class hierarchy up to {@code Object.class} to ensure
     * that inherited event handlers are correctly registered.
     * </p>
     */
    public static void connect(Class<?> listenerClass) {
        global.register(listenerClass);
    }
    
    /**
     * Registers all static methods marked with @SubscribeEvent in the object.
     * <p>
     * Logic: Traverses the class hierarchy up to {@code Object.class} to ensure
     * that inherited event handlers are correctly registered.
     * </p>
     */
    public void register(Class<?> listenerClass) {
        if (listenerClass == null) {
            System.err.println("EventBus: Cannot register null class for static methods.");
            return;
        }
        
        for (Method method : listenerClass.getDeclaredMethods()) {
            // Only process static methods for static registration
            if (Modifier.isStatic(method.getModifiers()) && method.isAnnotationPresent(SubscribeEvent.class)) {
                Class<?>[] params = method.getParameterTypes();
                if (params.length == 1 && Event.class.isAssignableFrom(params[0])) {
                    Class<?> eventType = params[0];
                    method.setAccessible(true); // Allow access to private static methods
                    global.subscribers
                            .computeIfAbsent(eventType, k -> new ArrayList<>())
                            .add(new ListenerMethod(null, method)); // Pass null for instance for static methods
                }
            }
        }
    }
    
    /**
     * Registers all non-static methods marked with @SubscribeEvent in the object.
     * <p>
     * Logic: Traverses the class hierarchy up to {@code Object.class} to ensure
     * that inherited event handlers are correctly registered.
     * </p>
     */
    public void register(Object listenerInstance) {
        if (listenerInstance == null) return;
        
        Class<?> clazz = listenerInstance.getClass();
        
        // --- FIX: Traverse the class hierarchy to find inherited @SubscribeEvent methods ---
        while (clazz != null && clazz != Object.class) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(SubscribeEvent.class)) {
                    Class<?>[] params = method.getParameterTypes();
                    if (params.length == 1 && Event.class.isAssignableFrom(params[0])) {
                        Class<?> eventType = params[0];
                        
                        // Check if the method is static (should be handled by the other register overload)
                        if (!Modifier.isStatic(method.getModifiers())) {
                            method.setAccessible(true);
                            subscribers
                                    .computeIfAbsent(eventType, k -> new ArrayList<>())
                                    .add(new ListenerMethod(listenerInstance, method));
                        }
                    }
                }
            }
            clazz = clazz.getSuperclass(); // Move up to the superclass
        }
    }
    
    public void post(Event event) {
        Class<?> eventType = event.getClass();
        
        // Collect listeners for the event class AND all its superclasses/interfaces
        List<ListenerMethod> collectedMethods = new ArrayList<>();
        Set<Class<?>> classesToSearch = new HashSet<>();
        
        // Start by adding the exact event class
        classesToSearch.add(eventType);
        
        // Add all superclasses and interfaces up to (but excluding) Object
        Class<?> currentClass = eventType;
        while (currentClass != null && currentClass != Object.class) {
            classesToSearch.add(currentClass);
            
            // Add all implemented interfaces (e.g., if you have interfaces like Cancellable)
            classesToSearch.addAll(Arrays.asList(currentClass.getInterfaces()));
            
            currentClass = currentClass.getSuperclass();
        }
        
        // Search the listener map using the collected class types
        for (Class<?> type : classesToSearch) {
            List<ListenerMethod> methods = subscribers.get(type);
            if (methods != null) {
                collectedMethods.addAll(methods);
            }
        }
        
        if (!collectedMethods.isEmpty()) {
            // Use a new list to prevent ConcurrentModificationException if a listener unregisters itself
            for (ListenerMethod lm : new ArrayList<>(collectedMethods)) {
                try {
                    lm.method.invoke(lm.instance, event);
                } catch (Exception e) {
                    // Wrap in RuntimeException for propagation
                    throw new RuntimeException("Error invoking listener for event " + event.getClass().getSimpleName(), e);
                }
            }
        }
    }
    
    public void unregister(Object listenerInstance) {
        if (listenerInstance == null) return;
        
        for (List<ListenerMethod> methods : subscribers.values()) {
            methods.removeIf(lm -> lm.instance == listenerInstance);
        }
    }
    
    private record ListenerMethod(Object instance, Method method) {
    }
}
