package com.clubops.contract;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractBonusRepository extends JpaRepository<ContractBonus, Long> {

    List<ContractBonus> findByContractOrderByBonusTypeAsc(PlayerContract contract);
}