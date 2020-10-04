/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package elephantdb.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.13.0)", date = "2020-10-04")
public class MetaData implements org.apache.thrift.TBase<MetaData, MetaData._Fields>, java.io.Serializable, Cloneable, Comparable<MetaData> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("MetaData");

  private static final org.apache.thrift.protocol.TField DOMAIN_METADATAS_FIELD_DESC = new org.apache.thrift.protocol.TField("domain_metadatas", org.apache.thrift.protocol.TType.MAP, (short)1);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new MetaDataStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new MetaDataTupleSchemeFactory();

  private @org.apache.thrift.annotation.Nullable java.util.Map<java.lang.String,DomainMetaData> domain_metadatas; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    DOMAIN_METADATAS((short)1, "domain_metadatas");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // DOMAIN_METADATAS
          return DOMAIN_METADATAS;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.DOMAIN_METADATAS, new org.apache.thrift.meta_data.FieldMetaData("domain_metadatas", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.MapMetaData(org.apache.thrift.protocol.TType.MAP, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING), 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, DomainMetaData.class))));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(MetaData.class, metaDataMap);
  }

  public MetaData() {
  }

  public MetaData(
    java.util.Map<java.lang.String,DomainMetaData> domain_metadatas)
  {
    this();
    this.domain_metadatas = domain_metadatas;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public MetaData(MetaData other) {
    if (other.is_set_domain_metadatas()) {
      java.util.Map<java.lang.String,DomainMetaData> __this__domain_metadatas = new java.util.HashMap<java.lang.String,DomainMetaData>(other.domain_metadatas.size());
      for (java.util.Map.Entry<java.lang.String, DomainMetaData> other_element : other.domain_metadatas.entrySet()) {

        java.lang.String other_element_key = other_element.getKey();
        DomainMetaData other_element_value = other_element.getValue();

        java.lang.String __this__domain_metadatas_copy_key = other_element_key;

        DomainMetaData __this__domain_metadatas_copy_value = new DomainMetaData(other_element_value);

        __this__domain_metadatas.put(__this__domain_metadatas_copy_key, __this__domain_metadatas_copy_value);
      }
      this.domain_metadatas = __this__domain_metadatas;
    }
  }

  public MetaData deepCopy() {
    return new MetaData(this);
  }

  @Override
  public void clear() {
    this.domain_metadatas = null;
  }

  public int get_domain_metadatas_size() {
    return (this.domain_metadatas == null) ? 0 : this.domain_metadatas.size();
  }

  public void put_to_domain_metadatas(java.lang.String key, DomainMetaData val) {
    if (this.domain_metadatas == null) {
      this.domain_metadatas = new java.util.HashMap<java.lang.String,DomainMetaData>();
    }
    this.domain_metadatas.put(key, val);
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.Map<java.lang.String,DomainMetaData> get_domain_metadatas() {
    return this.domain_metadatas;
  }

  public void set_domain_metadatas(@org.apache.thrift.annotation.Nullable java.util.Map<java.lang.String,DomainMetaData> domain_metadatas) {
    this.domain_metadatas = domain_metadatas;
  }

  public void unset_domain_metadatas() {
    this.domain_metadatas = null;
  }

  /** Returns true if field domain_metadatas is set (has been assigned a value) and false otherwise */
  public boolean is_set_domain_metadatas() {
    return this.domain_metadatas != null;
  }

  public void set_domain_metadatas_isSet(boolean value) {
    if (!value) {
      this.domain_metadatas = null;
    }
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case DOMAIN_METADATAS:
      if (value == null) {
        unset_domain_metadatas();
      } else {
        set_domain_metadatas((java.util.Map<java.lang.String,DomainMetaData>)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case DOMAIN_METADATAS:
      return get_domain_metadatas();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case DOMAIN_METADATAS:
      return is_set_domain_metadatas();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof MetaData)
      return this.equals((MetaData)that);
    return false;
  }

  public boolean equals(MetaData that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_domain_metadatas = true && this.is_set_domain_metadatas();
    boolean that_present_domain_metadatas = true && that.is_set_domain_metadatas();
    if (this_present_domain_metadatas || that_present_domain_metadatas) {
      if (!(this_present_domain_metadatas && that_present_domain_metadatas))
        return false;
      if (!this.domain_metadatas.equals(that.domain_metadatas))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((is_set_domain_metadatas()) ? 131071 : 524287);
    if (is_set_domain_metadatas())
      hashCode = hashCode * 8191 + domain_metadatas.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(MetaData other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(is_set_domain_metadatas()).compareTo(other.is_set_domain_metadatas());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (is_set_domain_metadatas()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.domain_metadatas, other.domain_metadatas);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  @org.apache.thrift.annotation.Nullable
  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("MetaData(");
    boolean first = true;

    sb.append("domain_metadatas:");
    if (this.domain_metadatas == null) {
      sb.append("null");
    } else {
      sb.append(this.domain_metadatas);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!is_set_domain_metadatas()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'domain_metadatas' is unset! Struct:" + toString());
    }

    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class MetaDataStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public MetaDataStandardScheme getScheme() {
      return new MetaDataStandardScheme();
    }
  }

  private static class MetaDataStandardScheme extends org.apache.thrift.scheme.StandardScheme<MetaData> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, MetaData struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // DOMAIN_METADATAS
            if (schemeField.type == org.apache.thrift.protocol.TType.MAP) {
              {
                org.apache.thrift.protocol.TMap _map18 = iprot.readMapBegin();
                struct.domain_metadatas = new java.util.HashMap<java.lang.String,DomainMetaData>(2*_map18.size);
                @org.apache.thrift.annotation.Nullable java.lang.String _key19;
                @org.apache.thrift.annotation.Nullable DomainMetaData _val20;
                for (int _i21 = 0; _i21 < _map18.size; ++_i21)
                {
                  _key19 = iprot.readString();
                  _val20 = new DomainMetaData();
                  _val20.read(iprot);
                  struct.domain_metadatas.put(_key19, _val20);
                }
                iprot.readMapEnd();
              }
              struct.set_domain_metadatas_isSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, MetaData struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.domain_metadatas != null) {
        oprot.writeFieldBegin(DOMAIN_METADATAS_FIELD_DESC);
        {
          oprot.writeMapBegin(new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.STRING, org.apache.thrift.protocol.TType.STRUCT, struct.domain_metadatas.size()));
          for (java.util.Map.Entry<java.lang.String, DomainMetaData> _iter22 : struct.domain_metadatas.entrySet())
          {
            oprot.writeString(_iter22.getKey());
            _iter22.getValue().write(oprot);
          }
          oprot.writeMapEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class MetaDataTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public MetaDataTupleScheme getScheme() {
      return new MetaDataTupleScheme();
    }
  }

  private static class MetaDataTupleScheme extends org.apache.thrift.scheme.TupleScheme<MetaData> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, MetaData struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      {
        oprot.writeI32(struct.domain_metadatas.size());
        for (java.util.Map.Entry<java.lang.String, DomainMetaData> _iter23 : struct.domain_metadatas.entrySet())
        {
          oprot.writeString(_iter23.getKey());
          _iter23.getValue().write(oprot);
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, MetaData struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      {
        org.apache.thrift.protocol.TMap _map24 = new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.STRING, org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
        struct.domain_metadatas = new java.util.HashMap<java.lang.String,DomainMetaData>(2*_map24.size);
        @org.apache.thrift.annotation.Nullable java.lang.String _key25;
        @org.apache.thrift.annotation.Nullable DomainMetaData _val26;
        for (int _i27 = 0; _i27 < _map24.size; ++_i27)
        {
          _key25 = iprot.readString();
          _val26 = new DomainMetaData();
          _val26.read(iprot);
          struct.domain_metadatas.put(_key25, _val26);
        }
      }
      struct.set_domain_metadatas_isSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

