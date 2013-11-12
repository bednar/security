Security Library [![Build Status](https://api.travis-ci.org/bednar/security.png?branch=master)](https://travis-ci.org/bednar/security)
====

## Authorize Persist Events

Persist events -
[save](https://github.com/bednar/persistence/blob/master/src/main/java/com/github/bednar/persistence/event/SaveEvent.java),
[read](https://github.com/bednar/persistence/blob/master/src/main/java/com/github/bednar/persistence/event/ReadEvent.java),
[delete](https://github.com/bednar/persistence/blob/master/src/main/java/com/github/bednar/persistence/event/DeleteEvent.java),
[list](https://github.com/bednar/persistence/blob/master/src/main/java/com/github/bednar/persistence/event/ListEvent.java)
are protected by [authorization subquery](https://github.com/bednar/security/blob/master/src/main/java/com/github/bednar/security/contract/ResourceAuthorize.java).

### Example

#### Create New

Criterion for select [Authenticable Resource](https://github.com/bednar/security/blob/master/src/main/java/com/github/bednar/security/contract/Authenticable.java) which can create `Chat Room`.

    /**
     * Only account with principal (unique user identifier)  Admin can create new Chat Room
     */
    @Nonnull
    public Criterion createNew(@Nonnull final String principal)
    {
        return Restrictions.eq("account", "admin");
    }
    
#### Update

Criterion for select Resources which can `principal` update.

    /**
     * Principal (unique user identifier) can update Chat Rooms where is admin.
     */
    @Nonnull
    public Criterion update(@Nonnull final String principal)
    {
        return Restrictions.eq("admin", principal);
    }
    
#### Read

Criterion for select Resources which can `principal` read (list).

    /**
     * Principal (unique user identifier) can read Chat Rooms where is admin or is subscribed to in.
     */
    @Nonnull
    public Criterion read(@Nonnull final String principal)
    {
        DetachedCriteria subscribers = DetachedCriteria
            .forClass(ChatRoomUserAssoc.class, "roomUser")
            .setProjection(Property.forName("chat.id"))
            .add(Restrictions.eq("user", principal));

        return Restrinctions.or(
            Restrictions.eq("admin", principal),
            Subqueries.propertyIn("id", subscribers)
        );
    }

Admin can delete *Chat rooms*.

    @Nonnull
    public Criterion delete(@Nonnull final String principal)
    {
        return Restrictions.eq("admin", principal);
    }

## Maven Repository

    <repository>
        <id>public</id>
        <name>Public</name>
        <url>http://nexus-bednar.rhcloud.com/nexus/content/groups/public/</url>
    </repository>

## License

    Copyright (c) 2013, Jakub Bednář
    All rights reserved.

    Redistribution and use in source and binary forms, with or without modification,
    are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this
      list of conditions and the following disclaimer.

    * Redistributions in binary form must reproduce the above copyright notice, this
      list of conditions and the following disclaimer in the documentation and/or
      other materials provided with the distribution.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
    ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
    WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
    DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
    ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
    (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
    LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
    ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
    SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
