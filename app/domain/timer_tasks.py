from enum import Enum

State = Enum("State",
             [
                 "CREATED",
                 "RUNNING",
                 "WAITING",
                 "DELETED"
             ])

DelayType = Enum("DelayType",
                 [
                     "NONE",
                     "FIXED_DELAY",
                     "COMPUTED_DELAY"
                 ])

ComputationDelayType = Enum("ComputationDelayType",
                            [
                                "CURRENT_DAY_SPECIFIC_TIME",
                                "CURRENT_WEEK_SPECIFIC_DAY",
                                "CURRENT_MONTH_SPECIFIC_DAY",
                                "CURRENT_YEAR_SPECIFIC_DAY",
                                "SPECIFIC_DATETIME"
                            ])

CycleType = Enum("CycleType",
                 [
                     "NONE",
                     "FIXED_SECONDS",
                     "FIXED_MINUTES",
                     "FIXED_HOURS",
                     "FIXED_DAYS",
                     "FIXED_WEEKS",
                     "FIXED_MONTHS",
                     "FIXED_YEARS"
                 ])

DeadlineType = Enum("DeadlineType",
                    [
                        "NONE",
                        "SPECIFIC_DATETIME",
                        "SECONDS_TO_RUN"
                    ])

DefinitionErrorType = Enum("DefinitionErrorType",
                           [
                               "FIXED_DELAY_WITHOUT_DELAY_SECONDS"
                           ])
