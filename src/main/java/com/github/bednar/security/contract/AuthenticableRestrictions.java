package com.github.bednar.security.contract;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;
import org.apache.shiro.SecurityUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StringType;

/**
 * @author Jakub Bednář (25/11/2013 18:59)
 */
public final class AuthenticableRestrictions
{
    private AuthenticableRestrictions()
    {
    }

    /**
     * {@code account} is actual {@link org.apache.shiro.subject.Subject#getPrincipal() Subject principal}
     *
     * @see #BY_ACCOUNT(String)
     */
    @Nonnull
    public static Criterion BY_ACCOUNT()
    {
        String principal = SecurityUtils.getSubject().getPrincipal().toString();

        return BY_ACCOUNT(principal);
    }

    /**
     * @param account {@link com.github.bednar.security.contract.Authenticable#getAccount()}
     *
     * @return sql restrictions for select {@code Authenticable Resource} from database
     *
     * @see com.github.bednar.persistence.contract.Resource
     * @see Authenticable
     */
    @Nonnull
    public static Criterion BY_ACCOUNT(@Nonnull final String account)
    {
        Preconditions.checkNotNull(account);

        return Restrictions.sqlRestriction("{alias}.account = ?", account, StringType.INSTANCE);
    }
}
