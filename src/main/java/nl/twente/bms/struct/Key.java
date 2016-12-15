package nl.twente.bms.struct;

/**
 * @author zhaofeng
 * @since ${version}
 */
public class Key
{
    public final String target, name, type, id;

    public Key(String target, String name, String id, String type)
    {
        this.target = target;
        this.name = name;
        this.type = type;
        this.id = id;
    }
}

