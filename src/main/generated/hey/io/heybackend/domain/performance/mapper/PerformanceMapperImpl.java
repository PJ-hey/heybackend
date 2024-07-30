package hey.io.heybackend.domain.performance.mapper;

import hey.io.heybackend.domain.performance.domain.Performance;
import hey.io.heybackend.domain.performance.dto.PerformanceDetailResponse;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-07-30T12:48:09+0900",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.8.jar, environment: Java 22.0.1 (Oracle Corporation)"
)
public class PerformanceMapperImpl implements PerformanceMapper {

    @Override
    public PerformanceDetailResponse toPerformanceDto(Performance performance) {
        if ( performance == null ) {
            return null;
        }

        PerformanceDetailResponse.PerformanceDetailResponseBuilder performanceDetailResponse = PerformanceDetailResponse.builder();

        performanceDetailResponse.id( performance.getId() );
        performanceDetailResponse.title( performance.getTitle() );
        performanceDetailResponse.startDate( performance.getStartDate() );
        performanceDetailResponse.endDate( performance.getEndDate() );
        performanceDetailResponse.theater( performance.getTheater() );
        performanceDetailResponse.cast( performance.getCast() );
        performanceDetailResponse.runtime( performance.getRuntime() );
        performanceDetailResponse.age( performance.getAge() );
        performanceDetailResponse.price( performance.getPrice() );
        performanceDetailResponse.poster( performance.getPoster() );
        performanceDetailResponse.visit( performance.getVisit() );
        performanceDetailResponse.status( performance.getStatus() );
        performanceDetailResponse.schedule( performance.getSchedule() );

        return performanceDetailResponse.build();
    }
}
