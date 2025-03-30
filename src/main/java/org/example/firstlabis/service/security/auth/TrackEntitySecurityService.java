package org.example.firstlabis.service.security.auth;


import lombok.extern.slf4j.Slf4j;
import org.example.firstlabis.model.audit.TrackEntity;
import org.example.firstlabis.model.security.Role;
import org.example.firstlabis.model.security.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


/**
 * Абстрактный класс задающий логику проверки прав на изменения сущностей пользователями с разными ролями
 *
 * @param <T>  сущность, которую хотят изменить
 * @param <ID> индикатор сущности
 */
@Service
@Slf4j
public abstract class TrackEntitySecurityService<T extends TrackEntity, ID> {
    protected abstract T findById(ID id);

    /**
     * Проверка, является ли текущий пользователь владельцем сущности над которой хочет совершить действия
     */
    public boolean isOwner(ID ownedEntityId) {
        var entity = findById(ownedEntityId);
        var currentUser = getCurrentUser();

        return isOwner(currentUser, entity);
    }

    /**
     * Проверка есть ли у пользователя права на редактирование данной сущности
     */
    public boolean hasEditRights(ID ownedEntityId) {
        var entity = findById(ownedEntityId);
        var currentUser = getCurrentUser();

        return hasEditRights(currentUser, entity);
    }

    /**
     * Проверяет есть ли у пользователя права на редактирования данной сущности на основе роли пользователя
     * и разрешения на редактирование, которое установлено на данной сущности
     */
    private boolean hasEditRights(User user, TrackEntity entity) {
        boolean isOwner = isOwner(user, entity);
        log.info(user.getRole().toString());
        boolean isAdminAndIsAdminEditAllowed = user.getRole() == Role.ADMIN && entity.isEditAdminStatus();
        return isOwner || isAdminAndIsAdminEditAllowed;
    }

    /**
     * Проверка является ли пользователь владельцем сущности или нет
     */
    private boolean isOwner(User user, TrackEntity entity) {
        return entity.getOwnerUsername().equals(user.getUsername());
    }

    /**
     * Метод возвращающий текущего пользователя
     */
    private User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}
