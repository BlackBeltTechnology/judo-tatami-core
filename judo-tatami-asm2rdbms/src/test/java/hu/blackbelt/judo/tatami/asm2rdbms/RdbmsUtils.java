package hu.blackbelt.judo.tatami.asm2rdbms;

import hu.blackbelt.judo.meta.rdbms.RdbmsField;
import hu.blackbelt.judo.meta.rdbms.RdbmsTable;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.meta.rdbms.support.RdbmsModelResourceSupport;
import lombok.Getter;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;

import java.util.Optional;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class RdbmsUtils {
    @Getter
    private RdbmsModelResourceSupport rdbmsModelResourceSupport;

    public RdbmsUtils(RdbmsModel rdbmsModel) {
        rdbmsModelResourceSupport = RdbmsModelResourceSupport.rdbmsModelResourceSupportBuilder()
                .resourceSet(rdbmsModel.getResourceSet())
                .uri(rdbmsModel.getUri())
                .build();
    }

    /**
     * Update RdbmsModelResourceSupport based on given RdbmsModel
     * @param rdbmsModel Build RdbmsModelResourceSupport based on this model
     */
    public void updateRdbmsModelResourceSupport(RdbmsModel rdbmsModel) {
        rdbmsModelResourceSupport = RdbmsModelResourceSupport.rdbmsModelResourceSupportBuilder()
                .resourceSet(rdbmsModel.getResourceSet())
                .uri(rdbmsModel.getUri())
                .build();
    }

    //////////////////////////////////////////////////
    //////////////////// TABLES //////////////////////

    /**
     * Get all RdbmsTable from RdbmsModel
     * @return all RdbmsTable if exists
     */
    public Optional<EList<RdbmsTable>> getRdbmsTables() {
        BasicEList<RdbmsTable> rdbmsTables = new BasicEList<>();
        rdbmsModelResourceSupport.getStreamOfRdbmsRdbmsTable().forEach(rdbmsTables::add);
        return !(rdbmsTables.isEmpty())
                ? Optional.of(rdbmsTables)
                : Optional.empty();
    }

    /**
     * Get certain RdbmsTable
     * @param rdbmsTableName RdbmsTable's name to search for (packagename.classname)
     * @return RdbmsTable if exists
     */
    public Optional<RdbmsTable> getRdbmsTable(String rdbmsTableName) {
        return getRdbmsTables().isPresent()
                ? getRdbmsTables().get().stream().filter(o -> rdbmsTableName.equals(o.getName())).findAny()
                : Optional.empty();
    }

    /**
     * Get certain RdbmsTable
     * @param rdbmsTableUUID RdbmsTable's UUID to search for (packagename.classname)
     * @return RdbmsTable if exists
     */
    public Optional<RdbmsTable> getRdbmsTableWithUUID(String rdbmsTableUUID) {
        return getRdbmsTables().isPresent()
                ? getRdbmsTables().get().stream().filter(o -> rdbmsTableUUID.equals(o.getUuid())).findAny()
                : Optional.empty();
    }

    //////////////////////////////////////////////////
    //////////////////// FIELDS //////////////////////

    /**
     * Get all RdbmsField from certain RdbmsTable
     * @param rdbmsTableName RdbmsTable's name to get all RdbmsField from (packagename.classname)
     * @return All RdbmsField if exists
     */
    public Optional<EList<RdbmsField>> getRdbmsFields(String rdbmsTableName) {
        return getRdbmsTable(rdbmsTableName).isPresent() && !(getRdbmsTable(rdbmsTableName).get().getFields().isEmpty())
                ? Optional.of(getRdbmsTable(rdbmsTableName).get().getFields())
                : Optional.empty();
    }

    /**
     * Get all RdbmsField from certain RdbmsTable
     * @param rdbmsTableUUID RdbmsTable's UUID to get all RdbmsField from (packagename.classname)
     * @return All RdbmsField if exists
     */
    public Optional<EList<RdbmsField>> getRdbmsFieldsWithUUID(String rdbmsTableUUID) {
        return getRdbmsTableWithUUID(rdbmsTableUUID).isPresent() && !(getRdbmsTableWithUUID(rdbmsTableUUID).get().getFields().isEmpty())
                ? Optional.of(getRdbmsTableWithUUID(rdbmsTableUUID).get().getFields())
                : Optional.empty();
    }

    /**
     * Get certain RdbmsField from given RdbmsTable
     * During search, rdbmsTableName and rdbmsFieldName are concatenated with '#' between them
     * Usage of this method is not recommended
     * Use {@link #getRdbmsFieldWithUUID(String)} instead
     * @param rdbmsTableName RdbmsTable's name to search in (packagename.classname)
     * @param rdbmsFieldName RdbmsField's name to search for
     * @return RdbmsField if exists
     */
    @Deprecated
    public Optional<RdbmsField> getRdbmsField(String rdbmsTableName, String rdbmsFieldName) {
        final String FQNAME = rdbmsTableName + "#" + rdbmsFieldName;
        return (getRdbmsFields(rdbmsTableName).isPresent() && getRdbmsFields(rdbmsTableName).get().stream().anyMatch(o -> FQNAME.equals(o.getName())))
                ? Optional.of(getRdbmsFields(rdbmsTableName).get().stream().filter(o -> FQNAME.equals(o.getName())).findAny().get())
                : Optional.empty();
    }

    /**
     * Get certain RdbmsField from given RdbmsTable
     * @param rdbmsFieldUUID RdbmsField's uuid to search for
     * @return RdbmsField if exists
     */
    public Optional<RdbmsField> getRdbmsFieldWithUUID(String rdbmsFieldUUID) {
        if(!rdbmsFieldUUID.matches("([a-zA-Z0-9_]+\\.)*[a-zA-Z0-9_]+#[a-zA-Z0-9_]+"))
            return Optional.empty();

        final String rdbmsTableUUID = rdbmsFieldUUID.split("#")[0];
        return (getRdbmsFieldsWithUUID(rdbmsTableUUID).isPresent() && getRdbmsFieldsWithUUID(rdbmsTableUUID).get().stream().anyMatch(o -> rdbmsFieldUUID.equals(o.getUuid())))
                ? Optional.of(getRdbmsFieldsWithUUID(rdbmsTableUUID).get().stream().filter(o -> rdbmsFieldUUID.equals(o.getUuid())).findAny().get())
                : Optional.empty();
    }

}
