import React from 'react';
import { DropdownItem } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { NavLink as Link } from 'react-router-dom';
import { NavDropdown } from '../header-components';

export const EntitiesMenu = props => (
  // tslint:disable-next-line:jsx-self-close
  <NavDropdown icon="th-list" name="Entities" id="entity-menu">
    <DropdownItem tag={Link} to="/entity/jhi-permission-my-suffix">
      <FontAwesomeIcon icon="asterisk" />&nbsp;Jhi Permission My Suffix
    </DropdownItem>
    <DropdownItem tag={Link} to="/entity/jhi-resource-my-suffix">
      <FontAwesomeIcon icon="asterisk" />&nbsp;Jhi Resource My Suffix
    </DropdownItem>
    <DropdownItem tag={Link} to="/entity/jhi-auth-perm-res-my-suffix">
      <FontAwesomeIcon icon="asterisk" />&nbsp;Jhi Auth Perm Res My Suffix
    </DropdownItem>
    {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
  </NavDropdown>
);
