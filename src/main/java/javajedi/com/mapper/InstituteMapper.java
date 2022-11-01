package javajedi.com.mapper;

import javajedi.com.data.InstituteData;
import javajedi.com.model.Institute;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Mapper(componentModel = "spring")
public interface InstituteMapper {

    Institute toInstitute(InstituteData instituteData);

    InstituteData toBasicInstituteData(Institute institute);

    InstituteData toInstituteData(Institute institute);

    default List<InstituteData> mapInstitutes(List<Institute> institutes) {
        return !isEmpty(institutes) ?
                institutes.stream().map(this::toInstituteData).collect(Collectors.toList()) : List.of();
    }

}
